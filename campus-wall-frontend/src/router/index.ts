import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue')
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/RegisterView.vue')
    },
    {
      path: '/post/:id',
      name: 'post-detail',
      component: () => import('@/views/PostDetailView.vue')
    },
    {
      path: '/create-post',
      name: 'create-post',
      component: () => import('@/views/CreatePostView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('@/views/ProfileView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/my-posts',
      name: 'my-posts',
      component: () => import('@/views/MyPostsView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/my-favorites',
      name: 'my-favorites',
      component: () => import('@/views/MyFavoritesView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/my-comments',
      name: 'my-comments',
      component: () => import('@/views/MyCommentsView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/my-products',
      name: 'my-products',
      component: () => import('@/views/MyProductsView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/marketplace',
      name: 'marketplace',
      component: () => import('@/views/MarketplaceView.vue')
    },
    {
      path: '/create-product',
      name: 'create-product',
      component: () => import('@/views/CreateProductView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/product/:id',
      name: 'product-detail',
      component: () => import('@/views/ProductDetailView.vue')
    },
    {
      path: '/admin',
      name: 'admin',
      component: () => import('@/views/AdminView.vue'),
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    {
      path: '/clubs',
      name: 'clubs',
      component: () => import('@/views/ClubsView.vue')
    },
    {
      path: '/club-manage',
      name: 'club-manage',
      component: () => import('@/views/ClubManageView.vue'),
      meta: { requiresAuth: true, requiresClubAdmin: true }
    },
    {
      path: '/club-recruitment/:id',
      name: 'club-recruitment-detail',
      component: () => import('@/views/ClubRecruitmentDetailView.vue')
    },
    {
      path: '/user/:id',
      name: 'user-profile',
      component: () => import('@/views/UserProfileView.vue')
    },
    {
      path: '/carpool',
      name: 'carpool',
      component: () => import('@/views/CarpoolView.vue')
    },
    {
      path: '/create-carpool',
      name: 'create-carpool',
      component: () => import('@/views/CreateCarpoolView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/carpool/:id',
      name: 'carpool-detail',
      component: () => import('@/views/CarpoolDetailView.vue')
    },
    {
      path: '/love-letter',
      name: 'love-letter',
      component: () => import('@/views/LoveLetterView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/chat-assistant',
      name: 'chat-assistant',
      component: () => import('@/views/ChatAssistantView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/messages',
      name: 'messages',
      component: () => import('@/views/MessagesView.vue'),
      meta: { requiresAuth: true }
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userRole = localStorage.getItem('role')

  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.meta.requiresAdmin && userRole !== 'SUPER_ADMIN') {
    next('/')
  } else if (to.meta.requiresClubAdmin && userRole !== 'CLUB_ADMIN') {
    next('/')
  } else {
    next()
  }
})

export default router
