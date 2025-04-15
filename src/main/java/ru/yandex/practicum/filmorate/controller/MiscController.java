package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MiscStorage;

import java.util.Collection;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class MiscController {
    private final MiscStorage miscStorage;

    @GetMapping("/genres")
    public Collection<Genre> findAllGenres() {
        return miscStorage.findAllGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre findGenre(@PathVariable int id) {
        return miscStorage.findGenre(id);
    }

    @GetMapping("/mpa")
    public Collection<Mpa> findAllMpa() {
        return miscStorage.findAllMpa();
    }

    @GetMapping("/mpa/{id}")
    public Mpa findMpa(@PathVariable int id) {
        return miscStorage.findMpa(id);
    }
}
