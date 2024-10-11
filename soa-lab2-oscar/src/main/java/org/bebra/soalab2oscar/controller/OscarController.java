package org.bebra.soalab2oscar.controller;

import lombok.RequiredArgsConstructor;
import org.bebra.soalab2oscar.service.OscarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.bebra.soacommons.model.dto.PersonDto;

import java.util.List;

/**
 * @author max_pri
 */
@RestController
@RequestMapping("/api/v1/oscar")
@RequiredArgsConstructor
public class OscarController {

    private final OscarService oscarService;

    @GetMapping("/directors/get-loosers")
    public ResponseEntity<List<PersonDto>> getLoosers() {
        return ResponseEntity.ok(oscarService.getLoosers());
    }

    @GetMapping("/movies/reward-thriller")
    public ResponseEntity<Void> rewardThriller() {
        oscarService.rewardThriller();
        return ResponseEntity.ok().build();
    }
}
