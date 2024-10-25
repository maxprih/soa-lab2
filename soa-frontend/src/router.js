import { createWebHistory, createRouter } from 'vue-router'

import MovieList from './components/MovieList.vue'

import LooserList from './components/LooserList.vue'
import MovieDetails from './components/MovieDetails.vue'
import AddMovie from './components/AddMovie.vue'

const routes = [
  {
    path: '/movies',
    name: 'movie-list',
    component: MovieList,
  },
  {
    path: '/loosers',
    name: 'loosers',
    component: LooserList
  },
  {
    path: '/add',
    name: 'AddMovie',
    component: AddMovie,
  },
  {
    path: '/movie/:id',
    name: 'MovieDetails',
    component: MovieDetails,
  },
]

const router = createRouter({
  history: createWebHistory("/soa/"),
  routes,
})

export default router
