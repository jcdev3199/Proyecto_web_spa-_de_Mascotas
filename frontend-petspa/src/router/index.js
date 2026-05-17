import { createRouter, createWebHistory } from 'vue-router'
import { authState } from '../store'

import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Dashboard from '../views/Dashboard.vue'
import Register from '../views/Register.vue'
import Profile from '../views/Profile.vue'
import VerifyAccount from '../views/VerifyAccount.vue'
import AdminUsers from '../views/AdminUsers.vue'
import AuditLogs from '../views/AuditLogs.vue'
import UserList from '../views/admin/UserList.vue'
import UserManagement from '../views/admin/UserManagement.vue'
import PerfilEmpleado from '../views/PerfilEmpleado.vue'


const routes = [
  { path: '/', name: 'Home', component: Home },
  { path: '/login', name: 'Login', component: Login },
  { 
    path: '/dashboard', 
    name: 'Dashboard', 
    component: Dashboard, 
    meta: { requiresAuth: true } 
  },
  { path: '/register', name: 'Register', component: Register },
  { 
    path: '/perfil', 
    name: 'Profile', 
    component: Profile, 
    meta: { requiresAuth: true } 
  },
  { path: '/verify', name: 'VerifyAccount', component: VerifyAccount },
  
  { 
    path: '/admin/usuarios', 
    name: 'UserList', 
    component: UserList, 
    meta: { requiresAdmin: true, requiresAuth: true } 
  }, 
  { 
    path: '/admin/usuarios/nuevo', 
    name: 'UserRegistration', 
    component: UserManagement, 
    meta: { requiresAdmin: true, requiresAuth: true } 
  },
  { 
    path: '/admin/logs', 
    name: 'AuditLogs', 
    component: AuditLogs, 
    meta: { requiresAdmin: true, requiresAuth: true } 
  },
  { 
    path: '/admin/gestion', 
    name: 'AdminUsers', 
    component: AdminUsers, 
    meta: { requiresAdmin: true, requiresAuth: true } 
  },
  { 
    path: '/admin/usuarios/lista', 
    name: 'UserListOld', 
    component: UserList, 
    meta: { requiresAdmin: true, requiresAuth: true } 
  },

  {
  path: '/verificar-cuenta',
  name: 'Verificar',
  component: () => import('../views/Verificar.vue')
  },
  {
  path: '/configurar-password',
  name: 'SetPassword',
  component: () => import('../views/EstablecerPassword.vue')
  },
  
  { 
    path: '/perfil-empleado', 
    name: 'PerfilEmpleado', 
    component: PerfilEmpleado, 
    meta: { requiresAuth: true } 
  },

]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Reemplaza TODO tu router.beforeEach con esta función completa
router.beforeEach((to, from, next) => {
  // 1. Capturamos los datos de la URL
  const { token, id_usuario, email } = to.query;

  // 2. REGLA DE ORO: Si es para verificar cuenta, NO es un login de Google.
  // Solo procesamos el token como login si NO estamos en '/verificar-cuenta'
  // y si al menos tenemos un email o id_usuario (que manda Google).
  if (token && to.path !== '/verificar-cuenta' && email) {
    console.log(">>> [DEBUG ROUTER] Detectado login de Google, sincronizando...");
    authState.setAuth({
      token,
      id_usuario: parseInt(id_usuario),
      username: to.query.username,
      email,
      rol: to.query.rol
    });
    return next({ path: '/dashboard', replace: true });
  }

  // 3. Lógica normal de protección de rutas
  const tokenLocal = localStorage.getItem('token');
  const user = authState.user;
  const isAdmin = user && (user.rol === 'Administrador' || user.nombreRol === 'Administrador');

  if (to.meta.requiresAuth && !tokenLocal) {
    next('/login');
  } else if (to.meta.requiresAdmin && !isAdmin) {
    next('/dashboard');
  } else {
    next();
  }
});

export default router