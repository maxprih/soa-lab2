<template>
  <div class="movie-details-container">
    <div class="card border-primary mt-4">
      <Card>
        <template #title>
          <div class="card-header d-flex justify-content-between align-items-center">
            <div class="w-25 d-flex justify-content-start">
              <Button type="button" label="Go Back" icon="pi pi-arrow-left" @click="goBack"
                      class="p-button-outlined p-button-secondary"/>
            </div>
            <h1 class="display-4 text-center m-0 flex-grow-1">Add New Movie</h1>
            <div class="w-25 d-flex justify-content-end">
              <Button type="button" label="Save" icon="pi pi-check" @click="saveMovie"
                      class="p-button-outlined p-button-primary me-2" :disabled="!isFormValid"/>
            </div>
          </div>
        </template>
      </Card>

      <div class="card-body">
        <div v-if="validationErrors.length > 0" class="mb-3 text-center w-100 d-flex flex-row ">
          <Message class="ms-3 text-center" v-for="error in validationErrors" :key="error.text" severity="error"
                   :text="error.text">
            {{ error.text }}
          </Message>
        </div>
        <div class="row">
          <div class="col-md-6">
            <Card>
              <template #header>
                <h2 class="mb-3 text-center">Movie Details</h2>
                <Divider/>
              </template>
              <template #content>
                <ul class="list-group">
                  <li class="list-group-item">
                    <i class="bi bi-c-circle"></i>
                    <strong>Name:</strong>
                    <InputText class="ms-2" v-model="newMovie.name"/>
                  </li>
                  <li class="list-group-item">
                    <i class="bi bi-file-word"></i>
                    <strong>Tagline:</strong>
                    <InputText class="ms-2" v-model="newMovie.tagline"/>
                  </li>
                  <li class="list-group-item">
                    <i class="bi bi-trophy"></i>
                    <strong>Oscars Count:</strong>
                    <InputNumber class="ms-2" v-model="newMovie.oscarsCount"/>
                  </li>
                  <li class="list-group-item">
                    <i class="bi bi-postcard"></i>
                    <strong>USA Box Office:</strong>
                    <InputNumber class="ms-2" v-model="newMovie.usaBoxOffice"/>
                  </li>
                  <li class="list-group-item">
                    <i class="bi bi-film"></i>
                    <strong>Genre:</strong>
                    <Select class="ms-2" v-model="newMovie.genre" :options="genres" optionLabel="name"
                            optionValue="code" placeholder="Select a Genre"/>
                  </li>
                </ul>
              </template>
            </Card>
          </div>

          <div class="col-md-6">
            <Card>
              <template #header>
                <h2 class="mb-3 text-center">Screenwriter Details</h2>
                <Divider/>
              </template>
              <template #content>
                <ul class="list-group">
                  <li class="list-group-item">
                    <i class="bi bi-person"></i>
                    <strong>Name:</strong>
                    <InputText class="ms-2" v-model="newMovie.screenwriter.name"/>
                  </li>
                  <li class="list-group-item">
                    <i class="bi bi-person-raised-hand"></i>
                    <strong>Height:</strong>
                    <InputNumber class="ms-2" v-model="newMovie.screenwriter.height"/>
                  </li>
                  <li class="list-group-item">
                    <i class="bi bi-person-bounding-box"></i>
                    <strong>Weight:</strong>
                    <InputNumber class="ms-2" v-model="newMovie.screenwriter.weight"/>
                  </li>
                  <li class="list-group-item">
                    <i class="bi bi-cake"></i>
                    <strong>Birthday:</strong>
                    <DatePicker class="ms-2" v-model="newMovie.screenwriter.birthday" dateFormat="yy-mm-dd"/>
                  </li>
                  <li class="list-group-item">
                    <i class="bi bi-passport"></i>
                    <strong>Passport ID:</strong>
                    <InputText class="ms-2" v-model="newMovie.screenwriter.passportId"/>
                  </li>
                </ul>
              </template>
            </Card>
          </div>
        </div>


        <div class="row mt-5">
          <div class="col-12 text-center">
            <Card>
              <template #header>
                <h2 class="mb-3 text-center">Coordinates</h2>
                <Divider/>
              </template>
              <template #content>
                <div class="d-flex justify-content-center">
                  <InputNumber v-model="newMovie.coordinates.x" placeholder="X" class="me-2"/>
                  <InputNumber v-model="newMovie.coordinates.y" placeholder="Y"/>
                </div>
              </template>
            </Card>
          </div>
        </div>
        <div class="row mt-4">
          <div class="col-12">
            <Chart type="scatter" :data="chartData" :options="chartOptions" style="height: 400px"/>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {ref, computed} from 'vue'
import {useRouter} from 'vue-router'
import {useToast} from 'primevue/usetoast'
import Card from 'primevue/card'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import InputNumber from 'primevue/inputnumber'
import DatePicker from 'primevue/datepicker'
import Select from 'primevue/select'
import Chart from 'primevue/chart'
import Message from 'primevue/message'
import Divider from 'primevue/divider'
import MovieService from '../services/movie.service'

export default {
  name: 'AddMovie',
  components: {
    Card,
    Button,
    InputText,
    InputNumber,
    DatePicker,
    Select,
    Chart,
    Divider,
    Message
  },
  setup() {
    const router = useRouter();
    const toast = useToast();
    const newMovie = ref({
      name: '',
      oscarsCount: 0,
      usaBoxOffice: 0,
      tagline: '',
      genre: 'THRILLER',
      screenwriter: {
        name: '',
        birthday: new Date().toLocaleDateString('en-CA'),
        height: 0,
        weight: 0,
        passportId: ''
      },
      coordinates: {
        x: 0,
        y: 0
      }
    })

    const genres = [
      {name: 'Western', code: 'WESTERN'},
      {name: 'Tragedy', code: 'TRAGEDY'},
      {name: 'Drama', code: 'DRAMA'},
      {name: 'Thriller', code: 'THRILLER'},
      {name: 'Fantasy', code: 'FANTASY'},
    ]

    const showSaved = () => {
      toast.add({severity: 'success', summary: 'Movie added', life: 3000})
    }

    const saveMovie = async () => {
      if (isFormValid.value) {
        await MovieService.save(newMovie.value);
        showSaved();
        router.push({name: "movie-list"})
      }
    }

    const goBack = () => {
      router.go(-1);
    }

    const chartData = computed(() => ({
      datasets: [
        {
          label: 'Movie Coordinate',
          data:
            newMovie.value && newMovie.value
              ? [
                {
                  x: newMovie.value.coordinates.x,
                  y: newMovie.value.coordinates.y,
                },
              ]
              : newMovie.value
                ? [
                  {
                    x: newMovie.value.coordinates.x,
                    y: newMovie.value.coordinates.y,
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
        newMovie.value.coordinates.x = x
        newMovie.value.coordinates.y = y
      },
    }

    const roundToTwo = num => {
      return +(Math.round(num + 'e+2') + 'e-2')
    }

    const validationErrors = computed(() => {
      const errors = [];

      if (!newMovie.value.name || newMovie.value.name.trim() === '') {
        errors.push({text: 'Movie name cannot be empty'});
      }
      if (newMovie.value.oscarsCount <= 0) {
        errors.push({text: 'Oscars count must be greater than 0'});
      }
      if (!newMovie.value.usaBoxOffice || newMovie.value.usaBoxOffice <= 0) {
        errors.push({text: 'USA Box Office must be greater than 0'});
      }
      if (!newMovie.value.tagline) {
        errors.push({text: 'Tagline cannot be empty'});
      }
      if (!newMovie.value.genre) {
        errors.push({text: 'Genre must be selected'});
      }

      if (newMovie.value.coordinates.x > 178) {
        errors.push({text: 'X coordinate must not exceed 178'});
      }
      if (newMovie.value.coordinates.y > 842) {
        errors.push({text: 'Y coordinate must not exceed 842'});
      }

      if (!newMovie.value.screenwriter.name || newMovie.value.screenwriter.name.trim() === '') {
        errors.push({text: 'Screenwriter name cannot be empty'});
      }
      if (!newMovie.value.screenwriter.birthday) {
        errors.push({text: 'Screenwriter birthday must be provided'});
      }
      if (!newMovie.value.screenwriter.height || newMovie.value.screenwriter.height <= 0) {
        errors.push({text: 'Screenwriter height must be greater than 0'});
      }
      if (newMovie.value.screenwriter.weight <= 0) {
        errors.push({text: 'Screenwriter weight must be greater than 0'});
      }
      if (!newMovie.value.screenwriter.passportId) {
        errors.push({text: 'Passport ID cannot be empty'});
      }
      if (newMovie.value.screenwriter.passportId && newMovie.value.screenwriter.passportId.length > 20) {
        errors.push({text: 'Passport ID must not exceed 20 characters'});
      }


      return errors;
    });

    const isFormValid = computed(() => validationErrors.value.length === 0)

    return {
      newMovie,
      genres,
      saveMovie,
      goBack,
      chartOptions,
      chartData,
      isFormValid,
      validationErrors
    }
  }
}
</script>

<style scoped>
@import 'bootstrap/dist/css/bootstrap.min.css';
@import 'bootstrap-icons/font/bootstrap-icons.css';

i {
  margin-right: 5px
}
</style>
