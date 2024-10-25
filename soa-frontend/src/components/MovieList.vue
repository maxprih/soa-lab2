<template>
  <div class="card">
    <DataTable :value="movies" :lazy="true" :paginator="true" :rows="10" :totalRecords="totalRecords" :loading="loading"
      @page="onPage($event)" @sort="onSort($event)" @filter="onFilter($event)" :filters="filters" :sortField="sortField"
      :sortOrder="sortOrder" dataKey="id" :rowsPerPageOptions="[5, 10, 20, 50]" filterDisplay="menu"
      :showFilterMenu="true"
      paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
      currentPageReportTemplate="Page {currentPage} of {totalPages}">
      <template #paginatorstart>
        <div class="mw-25">
          <Button type="button" icon="pi pi-filter-slash" label="Clear Filters" class="p-button-outlined"
            @click="clearFilter()" />
        </div>
      </template>
      <template #paginatorend>
        <Button type="button" label="Special Operations" icon="pi pi-cog" @click="openSpecialOpsDialog()" />
        <Dialog pt:root:class="!border-0 !bg-transparent" v-model:visible="specialOpsDialogVisible"
          header="Special Operations" :style="{ width: '50vw' }">
          <Menu :model="menuItems" />
        </Dialog>

        <Dialog v-model:visible="genreLessDialogVisible" header="Get Movies by Genre Less Than">
          <Select class="w-50" v-model="selectedGenre" :options="genres" optionLabel="name" optionValue="code"
            placeholder="Select a Genre" />
          <Button label="Submit" class="ms-5" @click="getMoviesByGenreLess" />
        </Dialog>

        <Dialog v-model:visible="taglineDialogVisible" header="Count Movies by Tagline" :style="{ width: '25vw' }">
          <div class="p-fluid">
            <div class="p-field d-flex align-items-center">
              <InputText id="tagline" class="w-50" v-model="tagline" @input="debouncedCountMoviesByTagline"
                placeholder="Enter tagline" />
              <span v-if="taglineCount !== null" class="ms-5">
                <strong>
                  Count:
                  <span :style="{ color: 'green' }">{{
                    taglineCount
                    }}</span></strong>
              </span>
            </div>
          </div>
        </Dialog>
      </template>
      <template #empty>
        <div class="text-center p-4">No movies found.</div>
      </template>
      <template #loading>
        <div class="text-center p-4">Loading movies data. Please wait.</div>
      </template>
      <Column field="id" header="ID" sortable filter dataType="numeric">
        <template #body="slotProps">
          <router-link class="link-success link-offset-2 link-underline-opacity-0"
            :to="{ name: 'MovieDetails', params: { id: slotProps.data.id } }">
            {{ slotProps.data.id }}
          </router-link>
        </template>
        <template #filter="{ filterModel }">
          <InputNumber v-model="filterModel.value" placeholder="Search by id" />
        </template>
      </Column>
      <Column field="name" header="Name" sortable filter dataType="text">
        <template #filter="{ filterModel }">
          <InputText v-model="filterModel.value" placeholder="Search by name" />
        </template>
      </Column>
      <Column field="tagline" header="Tagline" sortable filter dataType="text">
        <template #filter="{ filterModel }">
          <InputText v-model="filterModel.value" placeholder="Search by tagline" />
        </template>
      </Column>
      <Column field="coordinates.x" header="X" sortable filter dataType="numeric">
        <template #filter="{ filterModel }">
          <InputNumber v-model="filterModel.value" placeholder="Search by X" />
        </template>
      </Column>
      <Column field="coordinates.y" header="Y" sortable filter dataType="numeric">
        <template #filter="{ filterModel }">
          <InputNumber v-model="filterModel.value" placeholder="Search by Y" />
        </template>
      </Column>
      <Column field="oscarsCount" header="Oscars Count" sortable dataType="numeric" filter>
        <template #filter="{ filterModel }">
          <InputNumber v-model="filterModel.value" placeholder="Search by oscars count" />
        </template>
      </Column>
      <Column field="usaBoxOffice" dataType="numeric" header="USA Box Office" sortable filter>
        <template #filter="{ filterModel }">
          <InputNumber v-model="filterModel.value" placeholder="Search by box office" />
        </template>
      </Column>
      <Column field="genre" header="Genre" :showFilterMatchModes="false" sortable filter>
        <template #filter="{ filterModel }">
          <MultiSelect v-model="filterModel.value" :options="genresJust" placeholder="Select Genre">
            <template #option="slotProps">
              <div class="flex items-center gap-2">
                <span>{{ slotProps.option }}</span>
              </div>
            </template>
          </MultiSelect>
        </template>
      </Column>
      <Column field="screenwriter.name" dataType="text" header="Screenwriter" sortable filter>
        <template #filter="{ filterModel }">
          <InputText v-model="filterModel.value" placeholder="Search by screenwriter name" />
        </template>
      </Column>
      <Column field="creationDate" header="Creation Date" sortable dataType="date" filter>
        <template #body="{ data }">
          {{ formatDate(data.creationDate) }}
        </template>
        <template #filter="{ filterModel }">
          <DatePicker v-model="filterModel.value" dateFormat="yy-mm-dd" placeholder="Search by date" />
        </template>
      </Column>
    </DataTable>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { FilterMatchMode } from '@primevue/core/api'
import { useRouter } from 'vue-router'
import { debounce } from 'lodash'
import { useToast } from 'primevue/usetoast'
import MultiSelect from 'primevue/multiselect'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import InputText from 'primevue/inputtext'
import InputNumber from 'primevue/inputnumber'
import Select from 'primevue/select'
import Dialog from 'primevue/dialog'
import Menu from 'primevue/menu'
import Button from 'primevue/button'
import MovieService from '../services/movie.service'
import OscarService from '../services/oscar.service'
import DatePicker from 'primevue/datepicker'

export default {
  name: 'MovieList',
  components: {
    DataTable,
    Column,
    InputText,
    InputNumber,
    DatePicker,
    MultiSelect,
    Button,
    Dialog,
    Menu,
    Select,
  },
  setup() {
    const specialOpsDialogVisible = ref(false)
    const genreLessDialogVisible = ref(false)
    const taglineDialogVisible = ref(false)
    const selectedGenre = ref(null)
    const tagline = ref('')
    const taglineCount = ref(null)

    const router = useRouter()
    const toast = useToast()
    const movies = ref([])
    const totalRecords = ref(0)
    const loading = ref(false)
    const filters = ref()
    const initFilters = () => {
      filters.value = {
        name: {
          constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }],
        },
        'coordinates.x': {
          constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }],
        },
        'coordinates.y': {
          constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }],
        },
        id: {
          constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }],
        },
        tagline: {
          constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }],
        },
        creationDate: {
          constraints: [{ value: null, matchMode: FilterMatchMode.DATE_IS }],
        },
        oscarsCount: {
          constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }],
        },
        usaBoxOffice: {
          constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }],
        },
        genre: { value: null, matchMode: FilterMatchMode.IN },
        'screenwriter.name': {
          constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }],
        },
      }
    }
    initFilters()

    const menuItems = [
      {
        label: 'Add New Movie',
        icon: 'pi pi-plus',
        command: () => {
          specialOpsDialogVisible.value = false;
          router.push({ name: 'AddMovie' });
        },
      },
      {
        label: 'Get Movie with Minimal Box Office',
        icon: 'pi pi-dollar',
        command: () => {
          minimalBoxOffice()
          specialOpsDialogVisible.value = false
        },
      },
      {
        label: 'Get Movies by Genre Less Than',
        icon: 'pi pi-filter',
        command: () => {
          genreLessDialogVisible.value = true
          specialOpsDialogVisible.value = false
        },
      },
      {
        label: 'Count Movies by Tagline',
        icon: 'pi pi-hashtag',
        command: () => {
          taglineDialogVisible.value = true
          specialOpsDialogVisible.value = false
        },
      },
      {
        label: 'Get Screenwriters with 0 Oscars',
        icon: 'pi pi-users',
        command: () => {
          specialOpsDialogVisible.value = false
          router.push({ name: 'loosers' });
        },
      },
      {
        label: 'Add 1 Oscar to Thriller Movies',
        icon: 'pi pi-plus',
        command: () => {
          rewardThrillerMovies()
          rewardNotification()
          specialOpsDialogVisible.value = false
        },
      },
    ]

    const sortField = ref('id')
    const sortOrder = ref(1)
    const genresJust = ref([
      'WESTERN',
      'TRAGEDY',
      'DRAMA',
      'THRILLER',
      'FANTASY',
    ])

    const rewardNotification = () => {
      toast.add({
        severity: 'success',
        summary: 'Reward!',
        detail: 'Thriller movies got 1 oscar',
        life: 3000,
      })
    }

    const genres = [
      { name: 'Western', code: 'WESTERN' },
      { name: 'Tragedy', code: 'TRAGEDY' },
      { name: 'Drama', code: 'DRAMA' },
      { name: 'Thriller', code: 'THRILLER' },
      { name: 'Fantasy', code: 'FANTASY' },
    ]

    const openSpecialOpsDialog = () => {
      specialOpsDialogVisible.value = true
    }

    const loadMovies = async event => {
      loading.value = true
      try {
        if (selectedGenre.value) {
          getMoviesByGenreLess(event)
        } else {
          const response = await MovieService.getFilter(
            event.rows || 10,
            event.first || 0,
            event.sortField || 'id',
            event.sortOrder || 1,
            event.filters || {},
          )
          movies.value = response.data.content
          totalRecords.value = response.data.page.totalElements
        }
      } catch (error) {
        console.error('Failed to fetch movies:', error)
      } finally {
        loading.value = false
      }
    }

    const onPage = event => {
      loadMovies(event)
    }

    const onSort = event => {
      loadMovies(event)
    }

    const clearFilter = () => {
      initFilters()
      selectedGenre.value = null
      loadMovies({ first: 0, rows: 10, sortField: 'id', sortOrder: 1 })
    }

    const onFilter = event => {
      filters.value = event.filters
      loadMovies({ filters: filters.value })
    }

    const minimalBoxOffice = async () => {
      const response = await MovieService.minBoxOffice()
      movies.value = [response.data]
      totalRecords.value = 1
    }

    const getMoviesByGenreLess = async event => {
      try {
        const response = await MovieService.getGenreLess(
          selectedGenre.value,
          event.rows || 10,
          event.first || 0,
          event.sortField || 'id',
          event.sortOrder || 1,
          event.filters || {},
        )
        movies.value = response.data.content
        totalRecords.value = response.data.page.totalElements
        genreLessDialogVisible.value = false
      } catch (error) {
        console.error('Error fetching movies by genre less:', error)
      }
    }

    const countMoviesByTagline = async () => {
      try {
        if (tagline.value.trim() !== '') {
          const response = await MovieService.countByTagline(tagline.value)
          taglineCount.value = response.data
        } else {
          taglineCount.value = null
        }
      } catch (error) {
        console.error('Error counting movies by tagline:', error)
      }
    }

    const debouncedCountMoviesByTagline = debounce(countMoviesByTagline, 500)

    const rewardThrillerMovies = async () => {
      try {
        await OscarService.rewardThriller()
        loadMovies({ first: 0, rows: 10, sortField: 'id', sortOrder: 1 })
      } catch (error) {
        console.error('Error rewarding thriller movies:', error)
      }
    }

    onMounted(() => {
      loadMovies({ first: 0, rows: 10, sortField: 'id', sortOrder: 1 })
    })

    const formatDate = value => {
      return new Date(value).toLocaleDateString('en-CA')
    }

    return {
      movies,
      totalRecords,
      loading,
      filters,
      sortField,
      sortOrder,
      genres,
      onPage,
      clearFilter,
      onSort,
      onFilter,
      formatDate,
      minimalBoxOffice,
      countMoviesByTagline,
      getMoviesByGenreLess,
      openSpecialOpsDialog,
      specialOpsDialogVisible,
      genreLessDialogVisible,
      taglineDialogVisible,
      selectedGenre,
      tagline,
      genresJust,
      menuItems,
      debouncedCountMoviesByTagline,
      taglineCount,
    }
  },
}
</script>

<style scoped>
@import 'bootstrap/dist/css/bootstrap.min.css';
@import 'bootstrap-icons/font/bootstrap-icons.css';
</style>
