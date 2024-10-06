document.addEventListener("DOMContentLoaded", function () {
    // Запрашиваем данные о пользователе через API
    fetch("/user/profile_user")
        .then(response => response.json())
        .then(user => {
            // Заполняем поля на странице
            document.getElementById("userId").textContent = user.id;
            document.getElementById("username").textContent = user.username;
            document.getElementById("password").textContent = user.password;
            // Обработка ролей (если есть несколько)
            let roles = user.authorities.map(role => role.authority.substring(5)).join(", ");
            document.getElementById("roles").textContent = roles;
            // Для отображения роли пользователя в навигации\
            document.getElementById("navbarUserName").textContent = user.username;
            document.getElementById("navbarUserRoles").textContent = roles;
        })
        .catch(error => console.error("Error fetching user data:", error));
});