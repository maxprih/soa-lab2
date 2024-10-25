<template>
  <div class="container-fluid d-flex flex-column vh-100 p-3 bebra">
    <Card>
      <template #title>
        <div class="card-header position-relative">
          <div class="position-absolute start-0 top-50 translate-middle-y">
            <Button type="button" label="Go Back" icon="pi pi-arrow-left" @click="goBack"
                    class="p-button-outlined p-button-secondary"/>
          </div>
          <h1 class="display-4 text-center m-0 flex-grow-1">
            Looser List
          </h1>
        </div>
      </template>
    </Card>
    <div class="row flex-grow-1 d-flex align-items-center">
      <div class="col">
        <div class="table-wrapper">
          <DataTable :value="loosers" :loading="loading" dataKey="id" class="p-datatable">
            <template #empty>
              <div class="text-center p-4">No loosers found.</div>
            </template>
            <template #loading>
              <div class="text-center p-4">Loading loosers data. Please wait.</div>
            </template>
            <Column field="id" header="ID" dataType="numeric"></Column>
            <Column field="name" header="Name" dataType="text"></Column>
            <Column field="birthday" header="Birthday" dataType="date">
              <template #body="{ data }">
                {{ formatDate(data.birthday) }}
              </template>
            </Column>
            <Column field="height" header="Height" dataType="numeric"></Column>
            <Column field="weight" header="Weight" dataType="numeric"></Column>
            <Column field="passportId" header="Passport ID" dataType="text"></Column>
          </DataTable>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {ref, onMounted} from 'vue'
import {useRouter} from 'vue-router'
import DataTable from 'primevue/datatable'
import Card from 'primevue/card'
import Column from 'primevue/column'
import Button from 'primevue/button'
import OscarService from '@/services/oscar.service'

export default {
  name: 'LooserList',
  components: {
    DataTable,
    Column,
    Button,
    Card
  },
  setup() {
    const router = useRouter()
    const loosers = ref([])
    const loading = ref(false)

    const loadLoosers = async () => {
      loading.value = true
      try {
        const response = await OscarService.getLoosers()
        loosers.value = response.data
      } catch (error) {
        console.error('Error loading loosers:', error)
      } finally {
        loading.value = false
      }
    }

    onMounted(() => {
      loadLoosers()
    })

    const formatDate = value => {
      return new Date(value).toLocaleDateString('en-CA')
    }

    const goBack = () => {
      router.go(-1)
    }

    return {
      loosers,
      loading,
      formatDate,
      goBack
    }
  },
}
</script>

<style scoped>
@import 'bootstrap/dist/css/bootstrap.min.css';
@import 'bootstrap-icons/font/bootstrap-icons.css';

.table-wrapper {
  max-width: 100%;
  height: 100%;
  overflow: auto;
}

.p-datatable {
  width: 100%;
}

:deep(.p-datatable-wrapper) {
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

:deep(.p-datatable-header) {
  background-color: #f8f9fa;
  border-top-left-radius: 8px;
  border-top-right-radius: 8px;
}


.bebra {
  overflow-x: hidden;
}
</style>
