<template>
  <div class="movie-details-container">
    <ConfirmDialog></ConfirmDialog>
    <div v-if="movie" class="card border-primary mt-4">
      <Card>
        <template #title>
          <div class="card-header d-flex justify-content-between align-items-center">
            <div class="w-25 d-flex justify-content-start">
              <Button type="button" label="Go Back" icon="pi pi-arrow-left" @click="goBack"
                      class="p-button-outlined p-button-secondary"/>
            </div>
            <h1 class="display-4 text-center m-0 flex-grow-1">
              {{ movie.name }}
            </h1>
            <div class="w-25 d-flex justify-content-end">
              <Button type="button" :label="editMode ? 'Save' : 'Edit'"
                      :icon="editMode ? 'pi pi-check' : 'pi pi-pencil'" @click="toggleEditMode"
                      class="p-button-outlined p-button-primary me-2"/>
              <Button type="button" label="Delete" icon="pi pi-trash" @click="deleteMovie"
                      class="p-button-outlined p-button-danger"/>
            </div>
          </div>
        </template>
        <template #subtitle>
          <div class="card-header d-flex justify-content-center align-items-center">
            <h5 v-if="!editMode" class="display-7 text-center m-0 flex-grow-1">
              {{ movie.tagline }}
            </h5>
            <InputText v-else v-model="editedMovie.tagline"/>
          </div>
        </template>
      </Card>

      <div class="card-body">
        <div class="row">
          <div class="col-md-6">
            <Card>
              <template #header>
                <h2 class="mb-3 text-center">Movie Details</h2>
                <Divider/>
              </template>
              <template #content>
                <div>
                  <ul class="list-group">
                    <li class="list-group-item">
                      <i class="bi bi-graph-up"></i>
                      <strong>ID:</strong>
                      <span>{{ movie.id }}</span>
                    </li>
                    <li class="list-group-item">
                      <i class="bi bi-calendar-date"></i>
                      <strong>Creation Date:</strong>
                      <span>{{ formatDate(movie.creationDate) }}</span>
                    </li>
                    <li class="list-group-item">
                      <i class="bi bi-trophy"></i>
                      <strong>Oscars Count:</strong>
                      <span v-if="!editMode">{{ movie.oscarsCount }}</span>
                      <InputNumber v-else v-model="editedMovie.oscarsCount"/>
                    </li>
                    <li class="list-group-item">
                      <i class="bi bi-postcard"></i>
                      <strong>USA Box Office:</strong>
                      <span v-if="!editMode">{{ movie.usaBoxOffice }}</span>
                      <InputNumber v-else v-model="editedMovie.usaBoxOffice"/>
                    </li>
                    <li class="list-group-item">
                      <i class="bi bi-film"></i> <strong>Genre:</strong>
                      <span v-if="!editMode">{{ movie.genre }}</span>
                      <Select v-else v-model="editedMovie.genre" :options="genres"
                              optionLabel="name" optionValue="code" placeholder="Select a Genre"/>
                    </li>
                  </ul>
                </div>
              </template>
            </Card>
          </div>
          <div class="col-md-6">
            <Card>
              <template #header>
                <h2 class="mb-3 text-center">
                  Screenwriter {{ movie.screenwriter.name }}
                  <i class="bi bi-person"></i>
                </h2>
                <Divider/>
              </template>
              <template #content>
                <div>
                  <ul class="list-group">
                    <li class="list-group-item">
                      <i class="bi bi-graph-up"></i>
                      <strong>ID:</strong>
                      <span>{{ movie.screenwriter.id }}</span>
                    </li>
                    <li class="list-group-item">
                      <i class="bi bi-cake"></i>
                      <strong>Birthday:</strong>
                      <span v-if="!editMode">{{
                          formatDate(movie.screenwriter.birthday)
                        }}</span>
                      <DatePicker v-else v-model="editedMovie.screenwriter.birthday"
                                  dateFormat="yy-mm-dd"/>
                    </li>
                    <li class="list-group-item">
                      <i class="bi bi-person-raised-hand"></i>
                      <strong>Height:</strong>
                      <span v-if="!editMode">{{ movie.screenwriter.height }} CM</span>
                      <InputNumber v-else v-model="editedMovie.screenwriter.height"
                                   suffix=" CM"/>
                    </li>
                    <li class="list-group-item">
                      <i class="bi bi-person-bounding-box"></i>
                      <strong>Weight:</strong>
                      <span v-if="!editMode">{{ movie.screenwriter.weight }} KG</span>
                      <InputNumber v-else v-model="editedMovie.screenwriter.weight"
                                   suffix=" KG"/>
                    </li>
                    <li class="list-group-item">
                      <i class="bi bi-passport"></i>
                      <strong>Passport ID:</strong>
                      <span v-if="!editMode">{{
                          movie.screenwriter.passportId
                        }}</span>
                      <InputText v-else v-model="editedMovie.screenwriter.passportId"/>
                    </li>
                  </ul>
                </div>
              </template>
            </Card>
          </div>
        </div>
        <div class="row mt-5">
          <div class="col-12 text-center">
            <h2>Coordinates</h2>
            <p v-if="!editMode">
              <strong>X:</strong> {{ movie.coordinates.x }},
              <strong>Y:</strong> {{ movie.coordinates.y }}
            </p>
            <div v-else class="d-flex justify-content-center">
              <InputNumber v-model="editedMovie.coordinates.x" placeholder="X" class="me-2"/>
              <InputNumber v-model="editedMovie.coordinates.y" placeholder="Y"/>
            </div>
          </div>
        </div>
        <div class="row mt-4">
          <div class="col-12">
            <Chart type="scatter" :data="chartData" :options="chartOptions" style="height: 400px"/>
          </div>
        </div>
      </div>
    </div>
    <div v-else class="loading-container">
      <ProgressSpinner/>
      <p>Loading movie details...</p>
    </div>
  </div>
</template>

<script>
import {ref, computed, onMounted} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {isEqual} from 'lodash'
import {useToast} from 'primevue/usetoast'
import {useConfirm} from 'primevue/useconfirm'
import ConfirmDialog from 'primevue/confirmdialog'
import ProgressSpinner from 'primevue/progressspinner'
import Chart from 'primevue/chart'
import Card from 'primevue/card'
import Divider from 'primevue/divider'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import InputNumber from 'primevue/inputnumber'
import DatePicker from 'primevue/datepicker'
import Select from 'primevue/select'
import MovieService from '../services/movie.service'

export default {
  name: 'MovieDetails',
  components: {
    ProgressSpinner,
    Chart,
    Card,
    Divider,
    Button,
    InputText,
    InputNumber,
    DatePicker,
    Select,
    ConfirmDialog,
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const movie = ref(null)
    const editedMovie = ref(null)
    const editMode = ref(false)

    const toast = useToast()
    const confirm = useConfirm()

    const showInfo = () => {
      toast.add({severity: 'info', summary: 'Movie not changed', life: 3000})
    }

    const showDeleted = () => {
      toast.add({severity: 'success', summary: 'Movie deleted', life: 3000})
    }

    const deleteMovie = () => {
      confirm.require({
        message: 'Do you want to delete this movie?',
        header: 'Danger Zone',
        icon: 'pi pi-info-circle',
        rejectLabel: 'Cancel',
        rejectProps: {
          label: 'Cancel',
          severity: 'secondary',
          outlined: true,
        },
        acceptProps: {
          label: 'Delete',
          severity: 'danger',
        },
        accept: async () => {
          try {
            await MovieService.delete(movie.value.id)
            showDeleted()
            router.push({name: 'movie-list'})
          } catch (error) {
            console.error('Error deleting movie:', error)
          }
        },
        reject: () => {
          toast.add({
            severity: 'error',
            summary: 'Rejected',
            detail: 'You have rejected',
            life: 3000,
          })
        },
      })
    }

    const genres = [
      {name: 'Western', code: 'WESTERN'},
      {name: 'Tragedy', code: 'TRAGEDY'},
      {name: 'Drama', code: 'DRAMA'},
      {name: 'Thriller', code: 'THRILLER'},
      {name: 'Fantasy', code: 'FANTASY'},
    ]

    const goBack = () => {
      router.go(-1)
    }

    const chartData = computed(() => ({
      datasets: [
        {
          label: 'Movie Coordinate',
          data:
            editMode.value && editedMovie.value
              ? [
                {
                  x: editedMovie.value.coordinates.x,
                  y: editedMovie.value.coordinates.y,
                },
              ]
              : movie.value
                ? [
                  {
                    x: movie.value.coordinates.x,
                    y: movie.value.coordinates.y,
                  },
                ]
                : [],
          backgroundColor: 'rgba(75, 192, 192, 1)',
          pointRadius: 8,
          pointHoverRadius: 12,
        },
      ],
    }))

    const chartOptions = {
      responsive: true,
      maintainAspectRatio: false,

      scales: {
        x: {
          type: 'linear',
          position: 'bottom',
          min: -842,
          max: 178,
          grid: {
            color: context =>
              context.tick.value === 0 ? 'cyan' : 'rgba(0,0,0,0.1)',
          },
        },
        y: {
          type: 'linear',
          position: 'left',
          min: -842,
          max: 842,
          grid: {
            color: context =>
              context.tick.value === 0 ? 'cyan' : 'rgba(0,0,0,0.1)',
          },
        },
      },
      plugins: {
        legend: {
          display: false,
        },
        tooltip: {
          callbacks: {
            label: context => `(${context.parsed.x}, ${context.parsed.y})`,
          },
        },
      },
      aspectRatio: 1,
      onClick: (event, activeElements, chart) => {
        if (editMode.value) {
          const xScale = chart.scales['x']
          const yScale = chart.scales['y']

          var x = Math.round(xScale.getValueForPixel(event.native.offsetX))
          var y = roundToTwo(yScale.getValueForPixel(event.native.offsetY))

          if (x >= 178) {
            x = 178
          }
          if (y >= 842) {
            y = 842
          }
          editedMovie.value.coordinates.x = x
          editedMovie.value.coordinates.y = y
          console.log(`Clicked at x: ${x}, y: ${y}`)
        }
      },
    }

    const fetchMovie = async () => {
      try {
        const response = await MovieService.getById(route.params.id)
        movie.value = response.data
        movie.value.creationDate = formatDate(movie.value.creationDate)
        movie.value.screenwriter.birthday = formatDate(
          movie.value.screenwriter.birthday,
        )
        editedMovie.value = JSON.parse(JSON.stringify(movie.value))
      } catch (error) {
        console.error('Error fetching movie:', error)
      }
    }

    const formatDate = date => {
      return new Date(date).toLocaleDateString('en-CA')
    }

    const toggleEditMode = async () => {
      if (editMode.value) {
        if (!isEqual(editedMovie.value, movie.value)) {
          try {
            if (isFormValid.value) {
              await MovieService.update(movie.value.id, editedMovie.value)
              movie.value = {...editedMovie.value}
              editMode.value = false
            }

          } catch (error) {
            console.error('Error updating movie:', error)

          }
        } else {
          editMode.value = false
          showInfo()
        }
      } else {
        editedMovie.value = JSON.parse(JSON.stringify(movie.value)) // Deep copy
        editMode.value = true
      }
    }

    const roundToTwo = num => {
      return +(Math.round(num + 'e+2') + 'e-2')
    }

    const validationErrors = computed(() => {
      const errors = [];

      if (!editedMovie.value.name || editedMovie.value.name.trim() === '') {
        errors.push({text: 'Movie name cannot be empty'});
      }
      if (editedMovie.value.oscarsCount <= 0) {
        errors.push({text: 'Oscars count must be greater than 0'});
      }
      if (!editedMovie.value.usaBoxOffice || editedMovie.value.usaBoxOffice <= 0) {
        errors.push({text: 'USA Box Office must be greater than 0'});
      }
      if (!editedMovie.value.tagline) {
        errors.push({text: 'Tagline cannot be empty'});
      }
      if (!editedMovie.value.genre) {
        errors.push({text: 'Genre must be selected'});
      }

      if (editedMovie.value.coordinates.x > 178) {
        errors.push({text: 'X coordinate must not exceed 178'});
      }
      if (editedMovie.value.coordinates.y > 842) {
        errors.push({text: 'Y coordinate must not exceed 842'});
      }

      if (!editedMovie.value.screenwriter.name || editedMovie.value.screenwriter.name.trim() === '') {
        errors.push({text: 'Screenwriter name cannot be empty'});
      }
      if (!editedMovie.value.screenwriter.birthday) {
        errors.push({text: 'Screenwriter birthday must be provided'});
      }
      if (!editedMovie.value.screenwriter.height || editedMovie.value.screenwriter.height <= 0) {
        errors.push({text: 'Screenwriter height must be greater than 0'});
      }
      if (editedMovie.value.screenwriter.weight <= 0) {
        errors.push({text: 'Screenwriter weight must be greater than 0'});
      }
      if (!editedMovie.value.screenwriter.passportId) {
        errors.push({text: 'Passport ID cannot be empty'});
      }
      if (editedMovie.value.screenwriter.passportId && editedMovie.value.screenwriter.passportId.length > 20) {
        errors.push({text: 'Passport ID must not exceed 20 characters'});
      }


      return errors;
    });

    const isFormValid = computed(() => validationErrors.value.length === 0)

    onMounted(() => {
      fetchMovie()
    })

    return {
      movie,
      editedMovie,
      editMode,
      genres,
      chartData,
      chartOptions,
      formatDate,
      roundToTwo,
      toggleEditMode,
      showInfo,
      deleteMovie,
      showDeleted,
      goBack,
      isFormValid,
      validationErrors
    }
  },
}
</script>

<style scoped>
@import 'bootstrap/dist/css/bootstrap.min.css';
@import 'bootstrap-icons/font/bootstrap-icons.css';

.movie-details-container {
  width: 100vw;
  height: 100vh;
  padding: 0;
  margin: 0;
  overflow-x: hidden;
}

.card {
  width: 100%;
  height: 100%;
  margin: 0;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.card-body {
  padding: 0;
}

.container-fluid {
  padding: 0;
}

.row {
  margin: 0;
}

.col-md-6 {
  padding: 1rem;
}

.chart-container {
  width: 100%;
  height: 400px;
  position: relative;
}

.loading-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

i {
  margin-right: 5px
}

@media (max-width: 768px) {
  .card-body {
    padding: 1rem;
  }
}
</style>
≥
