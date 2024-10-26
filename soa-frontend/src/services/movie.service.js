import axios from 'axios'

const API_URL = import.meta.env.VITE_MOVIE_URL || 'https://localhost:8443/soa-lab2-movie-1.0-SNAPSHOT/api/v1/movie'

class MovieService {
  get(size, page, sort) {
    const params = { page, size }
    if (sort) {
      params.sort = sort
    }
    return axios.get(API_URL, { params })
  }

  getFilter(rows, first, sortField, sortOrder, filters = {}) {
    const params = {
      size: rows,
      page: first / rows,
    }
    if (sortOrder) {
      params.sort = sortField
        ? `${sortField},${sortOrder === 1 ? 'asc' : 'desc'}`
        : ''
    }

    if (filters && Object.keys(filters).length > 0) {
      Object.entries(filters).forEach(([key, filterValue]) => {
        if (key === 'genre') {
          if (filterValue.value !== null && filterValue.value !== undefined) {
            if (Array.isArray(filterValue.value)) {
              params[`${key}`] = filterValue.value.join(',')
            } else {
              params[`${key}`] = filterValue.value
            }
            params[`${key}-filter`] = 'in'
          }
        } else if (
          filterValue.constraints &&
          filterValue.constraints.length > 0
        ) {
          filterValue.constraints.forEach(constraint => {
            if (constraint.value !== null && constraint.value !== undefined) {
              if (key === 'creationDate') {
                constraint.value = new Date(constraint.value).toLocaleDateString('en-CA')
              }
              params[`${key}`] = `${constraint.value}`
              params[`${key}-filter`] = `${constraint.matchMode}`
            }
          })
        }
      })
    }
    return axios.get(API_URL, { params })
  }

  getGenreLess(genre, rows, first, sortField, sortOrder, filters = {}) {
    const params = {
      size: rows,
      page: first / rows,
      genre: genre,
    }
    if (sortOrder) {
      params.sort = sortField
        ? `${sortField},${sortOrder === 1 ? 'asc' : 'desc'}`
        : ''
    }

    if (filters && Object.keys(filters).length > 0) {
      Object.entries(filters).forEach(([key, filterValue]) => {
        if (key === 'genre') {
          console.log()
        } else if (
          filterValue.constraints &&
          filterValue.constraints.length > 0
        ) {
          filterValue.constraints.forEach(constraint => {
            if (constraint.value !== null && constraint.value !== undefined) {
              if (key === 'creationDate') {
                constraint.value = new Date(constraint.value)
                  .toISOString()
                  .split('T')[0]
              }
              params[`${key}`] = `${constraint.value}`
              params[`${key}-filter`] = `${constraint.matchMode}`
            }
          })
        }
      })
    }
    return axios.get(API_URL + '/genre-less-then', { params })
  }

  minBoxOffice() {
    return axios.get(API_URL + '/min-box-office')
  }

  countByTagline(tagline) {
    return axios.get(API_URL + '/' + 'count-by-tagline', {
      params: { tagline: tagline },
    })
  }

  getById(id) {
    return axios.get(API_URL + '/' + id)
  }

  save(data) {
    return axios.post(API_URL, data)
  }

  delete(id) {
    return axios.delete(API_URL + '/' + id)
  }

  update(id, data) {
    return axios.put(API_URL + '/' + id, data)
  }
}
export default new MovieService()
