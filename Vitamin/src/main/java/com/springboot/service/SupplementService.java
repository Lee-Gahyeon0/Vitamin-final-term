package com.springboot.service;

import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.springboot.domain.Member;
import com.springboot.domain.NormalizedSupplement;
import com.springboot.domain.Supplement;
import com.springboot.dto.SupplementFormDto;
import com.springboot.repository.MemberRepository;
import com.springboot.repository.NormalizedSupplementRepository;
import com.springboot.repository.SupplementRepository;


@Service
@Transactional
public class SupplementService {

    private final SupplementRepository supplementRepository;
    private final MemberRepository memberRepository;
    private final NormalizedSupplementRepository normalizedSupplementRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;   // C:/vitamin-uploads

    public SupplementService(SupplementRepository supplementRepository,
                             MemberRepository memberRepository,
                             NormalizedSupplementRepository normalizedSupplementRepository) {
        this.supplementRepository = supplementRepository;
        this.memberRepository = memberRepository;
        this.normalizedSupplementRepository = normalizedSupplementRepository;
    }

    
 // =========================
    // 1) 내 영양제 등록
    // =========================
    @Transactional
    public Supplement createSupplement(Long memberId, SupplementFormDto form) {

        // 1. 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다. id=" + memberId));

        // 2. Supplement 엔티티 생성
        Supplement s = new Supplement();
        s.setMember(member);
        s.setName(form.getName());
        s.setBrand(form.getBrand());
        s.setMemo(form.getMemo());

        // 화면에서 넘어온 태그들(List<String>) -> "철분,비타민C" 형식으로 병합
        if (form.getTagCodes() != null && !form.getTagCodes().isEmpty()) {
            String joined = String.join(",", form.getTagCodes());
            s.setTags(joined);
        } else {
            s.setTags("");
        }

        // 이미지 파일 처리 (추가된 부분)
        MultipartFile imageFile = form.getImageFile();
        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = storeImageFile(imageFile);
            s.setImagePath(imagePath);   // DB의 image_path 컬럼에 매핑된 필드
        }

        // 3. 우선 저장 (id 필요할 수 있으니까)
        Supplement saved = supplementRepository.save(s);

        // 4. 자동 연결 시도 (정규화된 영양제와 매핑)
        autoLinkNormalizedSupplement(saved);

        return saved;
    }
    
    // =========================
    // 이미지 파일 저장 
    // =========================
    private String storeImageFile(MultipartFile file) {
        try {
            // 저장 폴더 없으면 생성
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String ext = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 파일명 충돌 방지용 랜덤 이름
            String storedFileName = UUID.randomUUID() + ext;

            Path target = uploadPath.resolve(storedFileName);
            file.transferTo(target.toFile());

            // URL 리턴 
            return "/vitamin-images/" + storedFileName;

        } catch (IOException e) {
            throw new RuntimeException("이미지 파일 저장 중 오류가 발생했습니다.", e);
        }
    }

 



    // =========================
    // 2) 자동 연결 로직
    // =========================
    @Transactional
    public void autoLinkNormalizedSupplement(Supplement supplement) {
        String name = supplement.getName();
        String brand = supplement.getBrand();

        if (name == null || name.isBlank() || brand == null || brand.isBlank()) {
            return; // 정보 부족하면 그냥 패스
        }

        // 제품명 + 회사명 둘 다 포함하는 NormalizedSupplement 후보 찾기
        List<NormalizedSupplement> candidates =
                normalizedSupplementRepository.findByProductNameContainingAndCompanyNameContaining(
                        name, brand
                );

        if (!candidates.isEmpty()) {
            // 일단 첫 번째 후보로 연결
            supplement.setNormalizedSupplement(candidates.get(0));
        }
    }

    // =========================
    // 3) 수동 연결 (검색 후 선택)
    // =========================
    @Transactional
    public void manualLinkNormalizedSupplement(Long supplementId, Long normalizedId) {

        Supplement supplement = findById(supplementId);

        NormalizedSupplement ns = normalizedSupplementRepository.findById(normalizedId)
                .orElseThrow(() -> new IllegalArgumentException("정규화된 영양제를 찾을 수 없습니다. id=" + normalizedId));

        supplement.setNormalizedSupplement(ns);
    }

    // =========================
    // 4) NormalizedSupplement 검색
    // =========================
    @Transactional(readOnly = true)
    public List<NormalizedSupplement> searchNormalizedSupplements(String keyword, String type) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }

        return switch (type) {
            case "company" -> normalizedSupplementRepository.findByCompanyNameContaining(keyword);
            case "tag" -> normalizedSupplementRepository.findByTagsContaining(keyword);
            default -> normalizedSupplementRepository.findByProductNameContaining(keyword); // 기본: 제품명
        };
    }

    // =========================
    // 5) 특정 회원의 영양제 목록
    // =========================
    @Transactional(readOnly = true)
    public List<Supplement> getSupplements(Long memberId) {
        // 삭제되지 않은(소프트 삭제 X) 영양제만 조회
        return supplementRepository.findByMemberIdAndDeletedFalse(memberId);
    }

    // =========================
    // 6) 영양제 한 개 조회
    // =========================
    @Transactional(readOnly = true)
    public Supplement findById(Long id) {
        // 삭제되지 않은 영양제만 조회
        return supplementRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("영양제를 찾을 수 없습니다. id=" + id));
    }

    // =========================
    // 7) 영양제 수정
    // =========================
    @Transactional
    public void updateSupplement(Long id, SupplementFormDto form) {

        Supplement supplement = findById(id);

        supplement.setName(form.getName());
        supplement.setBrand(form.getBrand());
        supplement.setMemo(form.getMemo());

        if (form.getTagCodes() != null && !form.getTagCodes().isEmpty()) {
            String joined = String.join(",", form.getTagCodes());
            supplement.setTags(joined);
        } else {
            supplement.setTags("");
        }

        // 새이미지 처리
        MultipartFile imageFile = form.getImageFile();
        if (imageFile != null && !imageFile.isEmpty()) {
            // 기존 파일 삭제
            String oldPath = supplement.getImagePath();
            if (oldPath != null && oldPath.startsWith("/vitamin-images/")) {
                String oldFileName = oldPath.substring("/vitamin-images/".length());
                try {
                    Files.deleteIfExists(Paths.get(uploadDir, oldFileName));
                } catch (IOException e) {
                    System.out.println("기존 이미지 삭제 실패: " + e.getMessage());
                }
            }

            // 새 파일 저장
            String newPath = storeImageFile(imageFile);
            supplement.setImagePath(newPath);
        }

        // 이름/브랜드 바뀌었으면 자동 연결 다시 시도
        autoLinkNormalizedSupplement(supplement);
    }


    // =========================
    // 8) 삭제 (소프트 삭제)
    // =========================
    @Transactional
    public void deleteSupplement(Long id) {

        Supplement supplement = supplementRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("영양제를 찾을 수 없습니다. id=" + id));

        // 이미지 삭제
        String imagePath = supplement.getImagePath();
        if (imagePath != null && imagePath.startsWith("/vitamin-images/")) {
            String fileName = imagePath.substring("/vitamin-images/".length());
            try {
                Files.deleteIfExists(Paths.get(uploadDir, fileName));  // uploadDir은 @Value 주입된 거
            } catch (IOException e) {
                System.out.println("이미지 삭제 실패: " + e.getMessage());
            }
        }

        supplement.setDeleted(true);
    }
}
