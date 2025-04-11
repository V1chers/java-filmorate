# java-filmorate
Template repository for Filmorate project.

![filmorate_ER_Diagram](/filmorate_ER_Diagram.png)

Получить все фильмы по моему проще через 3 запроса:
- Сначала получаем сами объекты фильмов
- Потом добавляем в их коллекции имена категорий и айди пользователей
```
SELECT f.id,
       f.name,
       f.description,
       f.releaseDate,
       f.duration,
       ar.rating
FROM films AS f
JOIN age_rating AS ar ON ar.id = films.age_rating;

SELECT fc.film_id,
       c.category_name
FROM film_category as fc
JOIN category as c ON c.id = fc.category_id;

SELECT *
FROM films_likes;
```
Пользователей с их друзьями тоже можно получить через два запроса
```
SELECT * 
FROM users

SELECT *
FROM friends
```
Конкретный фильмы или пользователя можно получить, добавив условие на наличие его id ко всем запросам:
- WHERE (user/film)_id = ?