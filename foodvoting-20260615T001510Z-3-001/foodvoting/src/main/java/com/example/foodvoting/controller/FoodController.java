package com.example.foodvoting.controller;

import com.example.foodvoting.entity.Food;
import com.example.foodvoting.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;

    @GetMapping("/")
    public String index(Model model) {
        List<Food> foods = foodService.findAllFoods();
        Food winnerFood = foodService.findWinnerFood().orElse(null);

        model.addAttribute("foods", foods);
        model.addAttribute("winnerFood", winnerFood);

        return "index";
    }

    @PostMapping("/food/add")
    public String addFood(@RequestParam String name,
                          @RequestParam MultipartFile image) {
        foodService.addFood(name, image);
        return "redirect:/";
    }

    @PostMapping("/food/vote")
    public String voteFood(@RequestParam Long id) {
        foodService.voteFood(id);
        return "redirect:/";
    }
}
