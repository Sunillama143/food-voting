package com.example.foodvoting.service;

import com.example.foodvoting.entity.Food;
import com.example.foodvoting.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final FoodRepository foodRepository;

    public List<Food> findAllFoods() {
        return foodRepository.findAll();
    }

    public Optional<Food> findWinnerFood() {
        return foodRepository.findAll()
                .stream()
                .max(Comparator.comparingInt(Food::getVoteCount));
    }

    public void addFood(String name, MultipartFile imageFile) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Food name is required");
        }

        Food food = Food.builder()
                .name(name.trim())
                .voteCount(0)
                .build();

        if (imageFile != null && !imageFile.isEmpty()) {
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;
            File uploadFolder = new File(uploadDir);

            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }

            String originalFilename = imageFile.getOriginalFilename();
            String safeFilename = originalFilename == null ? "food-image" : originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
            String fileName = UUID.randomUUID() + "_" + safeFilename;

            File saveFile = new File(uploadDir + fileName);
            try {
                imageFile.transferTo(saveFile);
                food.setImageName(fileName);
            } catch (IOException e) {
                throw new RuntimeException("Image upload failed", e);
            }
        }

        foodRepository.save(food);
    }

    public void voteFood(Long id) {
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Food not found with id: " + id));

        food.setVoteCount(food.getVoteCount() + 1);
        foodRepository.save(food);
    }
}
