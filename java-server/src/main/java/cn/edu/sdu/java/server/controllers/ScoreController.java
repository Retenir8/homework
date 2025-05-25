package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.services.ScoreService;
import cn.edu.sdu.java.server.services.UserDetailsImpl;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/score")
public class ScoreController {
    private final ScoreService scoreService;
    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @PostMapping("/getStudentItemOptionList")
    public OptionItemList getStudentItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        return scoreService.getStudentItemOptionList(dataRequest);
    }

    @PostMapping("/getCourseItemOptionList")
    public OptionItemList getCourseItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        return scoreService.getCourseItemOptionList(dataRequest);
    }

    @PostMapping("/getScoreList")
    public DataResponse getScoreList(@Valid @RequestBody DataRequest dataRequest) {
        return scoreService.getScoreList(dataRequest);
    }
    @PostMapping("/scoreSave")
    public DataResponse scoreSave(@Valid @RequestBody DataRequest dataRequest) {
        return scoreService.scoreSave(dataRequest);
    }
    @PostMapping("/scoreDelete")
    public DataResponse scoreDelete(@Valid @RequestBody DataRequest dataRequest) {
        return scoreService.scoreDelete(dataRequest);
    }
    @PostMapping("/searchMyScores")
    public DataResponse searchMyScores(@Valid @RequestBody DataRequest dataRequest) {
        System.out.println("searchMyScores");
        return scoreService.searchMyScores(dataRequest);
    }


}
