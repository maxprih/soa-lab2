<template>
  <div class="container-fluid">
    <Toast />
    <router-view></router-view>
  </div>
</template>

<script>
import Toast from 'primevue/toast'

export default {
  name: 'App',

  components: {
    Toast,
  },
}
</script>

<style></style>
