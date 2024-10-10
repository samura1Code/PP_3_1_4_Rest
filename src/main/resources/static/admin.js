document.addEventListener('DOMContentLoaded', function () {
    fetchCurrentUser();
    fetchUsers();
    loadRoles();
    setupCloseButtons();
});

// Функция для получения и отображения всех пользователей
function fetchUsers() {
    console.log('Fetching users...');
    fetch('api/admin/users') // Проверьте этот URL
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch users');
            }
            return response.json();
        })
        .then(response => {
            console.log('Users fetched:', response);
            const tableBody = document.getElementById('users-table-body');
            tableBody.innerHTML = ''; // Очищаем существующие строки
            response.forEach(user => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${user.id}</td>
                    <td>${user.username}</td>
                    <td>${user.roles.map(role => role.name).join(', ')}</td> 
                    <td><button class="btn btn-info" onclick="openEditUserPopup(${user.id})">Edit</button></td>
                    <td><button class="btn btn-danger" onclick="openDeleteUserPopup(${user.id})">Delete</button></td>
                `;
                tableBody.appendChild(row);
            });
        })
        .catch(error => {
            console.error('Error fetching users:', error);
            alert('Ошибка при загрузке пользователей');
        });
}


document.getElementById('new-user-form').addEventListener('submit', function (event) {
    event.preventDefault();
    const formData = new FormData(this);
    const rolesSelected = Array.from(document.getElementById('roles').selectedOptions).map(option => ({
        id: parseInt(option.value, 10)
    }));
    const user = {
        username: formData.get('username'),
        password: formData.get('password'),
        roles: rolesSelected
    };
    console.log('Creating user:', user);
    fetch('api/admin/users', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(user)
    })
        .then(response => {
            if (response.ok) {
                fetchUsers();
                alert('Пользователь успешно создан!');
                this.reset();
                closeModal('newUserPopup');
            } else {
                return response.json().then(data => {
                    if (data.errors) {
                        displayErrors(data.errors);
                    } else {
                        throw new Error('Не удалось создать пользователя');
                    }
                });
            }
        })
        .catch(error => {
            console.error('Error creating user:', error);
            alert('Ошибка при создании пользователя: ' + error.message);
        });

});

function loadRoles() {
    console.log('Loading roles...');
    fetch('api/admin/users/roles')
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch roles');
            }
            return response.json();
        })
        .then(roles => {
            console.log('Roles fetched:', roles);
            const roleSelect = document.getElementById('roles');
            roleSelect.innerHTML = ''; // Очищаем существующие опции
            roles.forEach(role => {
                const option = document.createElement('option');
                option.value = role.id;
                option.text = role.authority; // Поле 'authority' содержит название роли
                roleSelect.appendChild(option);
            });
        })
        .catch(error => {
            console.error('Error loading roles:', error);
            alert('Ошибка при загрузке ролей');
        });
}

// Функция для открытия модального окна редактирования пользователя и заполнения формы
function openEditUserPopup(userId) {
    console.log('Opening edit modal for user ID:', userId);
    fetch(`api/admin/users/${userId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch user');
            }
            return response.json();
        })
        .then(user => {
            console.log('User fetched for edit:', user);
            document.getElementById('editUserId').value = user.id;
            document.getElementById('editUsername').value = user.username;
         //   document.getElementById('editPassword').value = user.password;

            const editRolesSelect = document.getElementById('editRoles');
            Array.from(editRolesSelect.options).forEach(option => {
                option.selected = user.roles.some(role => role.id === parseInt(option.value, 10));
            });
            openModal('editUserModal');
        })
        .catch(error => {
            console.error('Error fetching user:', error);
            alert('Ошибка при загрузке данных пользователя');
        });
}

// Обработчик отправки формы редактирования пользователя
document.getElementById('editUserForm').addEventListener('submit', function (event) {
    event.preventDefault();
    const formData = new FormData(this);
    const userId = parseInt(formData.get('id'), 10);
    const rolesSelected = Array.from(document.getElementById('editRoles').selectedOptions).map(option => ({
        id: parseInt(option.value, 10)
    }));
    const user = {
        id: userId, // ID пользователя обязательно
        username: formData.get('editUsername'),
        password: formData.get('editPassword'),
        roles: rolesSelected
    };
    console.log('Updating user:', user);
    fetch(`api/admin/users/${userId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(user)
    })
        .then(response => {
            if (response.ok) {
                fetchUsers();
                alert('Пользователь успешно обновлен!');
                closeModal('editUserModal');
            } else {
                return response.json().then(data => {
                    if (data.errors) {
                        displayErrors(data.errors);
                    } else {
                        throw new Error('Ошибка при обновлении пользователя');
                    }
                });
            }
        })
        .catch(error => {
            console.error('Error updating user:', error);
            alert('Ошибка при обновлении пользователя: ' + error.message);
        });

});


// Функция для открытия модального окна
function openModal(modalId) {
    console.log('Opening modal:', modalId);
    const modal = document.getElementById(modalId);
    const overlay = document.getElementById('overlay');
    if (modal && overlay) {
        modal.style.display = 'block';
        overlay.style.display = 'block';
        document.body.style.overflow = 'hidden';
    }
}

// Функция для закрытия модального окна
function closeModal(modalId) {
    console.log('Closing modal:', modalId);
    const modal = document.getElementById(modalId);
    const overlay = document.getElementById('overlay');
    if (modal && overlay) {
        modal.style.display = 'none';
        overlay.style.display = 'none';
        document.body.style.overflow = 'auto';
    }
}

// Функция для установки обработчиков закрытия модальных окон
function setupCloseButtons() {
    const closeButtons = document.querySelectorAll('.close-popup');
    closeButtons.forEach(button => {
        button.addEventListener('click', function (event) {
            event.preventDefault();
            const modalId = this.getAttribute('data-modal');
            if (modalId) {
                closeModal(modalId);
            }
        });
    });

    // Закрытие модальных окон при клике на оверлей
    const overlay = document.getElementById('overlay');
    overlay.addEventListener('click', function () {
        const modals = document.querySelectorAll('.popup');
        modals.forEach(modal => {
            modal.style.display = 'none';
        });
        this.style.display = 'none';
        document.body.style.overflow = 'auto';
    });
}

// Функция для удаления пользователя
function openDeleteUserPopup(userId) {
    if (confirm('Вы уверены, что хотите удалить этого пользователя?')) {
        console.log('Deleting user ID:', userId);
        fetch(`api/admin/users/${userId}`, {
            method: 'DELETE'
        })
            .then(response => {
                if (response.ok) {
                    fetchUsers();
                    alert('Пользователь успешно удалён!');
                } else {
                    return response.json().then(data => {
                        throw new Error(data.message || 'Не удалось удалить пользователя');
                    });
                }
            })
            .catch(error => {
                console.error('Error deleting user:', error);
                alert('Ошибка при удалении пользователя: ' + error.message);
            });
    }
}

// Функция для получения и отображения текущего пользователя
function fetchCurrentUser() {
    console.log('Fetching current user info...');
    fetch('api/admin/currentUser')
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch current user info');
            }
            return response.json();
        })
        .then(user => {
            console.log('Current user fetched:', user);
            const usernameSpan = document.getElementById('currentUsername');
            const roleSpan = document.getElementById('currentUserRole');
            usernameSpan.textContent = user.username; // Используем имя пользователя вместо email
            roleSpan.textContent = user.roles.map(role => role.name).join(', '); // Преобразуем массив ролей в строку
        })
        .catch(error => {
            console.error('Error fetching current user info:', error);
        });
}

// Функция для отображения ошибок под полями
function displayErrors(errors) {
    if (errors.username) {
        document.getElementById('usernameError').textContent = errors.username;
    }
    if (errors.password) {
        document.getElementById('passwordError').textContent = errors.password;
    }
    if (errors.roles) {
        document.getElementById('rolesError').textContent = errors.roles;
    }
}




