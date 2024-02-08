<template>
  <v-app>
    <v-container fluid fill-height>
      <v-data-table
        :headers="headers"
        :items="users"
        :search="search"
        :loading="loading"
        class="elevation-1"
      >
        <template v-slot:item.action="{ item }">
          <v-icon small @click="editUser(item)">mdi-pencil</v-icon>
        </template>

        <template v-slot:top>
          <v-toolbar flat dark color="teal">
            <v-toolbar-title class="white--text">Benutzerliste</v-toolbar-title>
            <v-divider class="mx-4" inset vertical></v-divider>
            <v-spacer></v-spacer>
            <v-text-field
              v-model="search"
              append-icon="mdi-magnify"
              label="Suchen"
              single-line
              hide-details
              class="white--text"
            ></v-text-field>
            <v-spacer></v-spacer>
          </v-toolbar>
        </template>

      </v-data-table>
    </v-container>
  </v-app>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      search: '',
      loading: false,
      headers: [
        { text: 'Vorname', key: 'firstname' },
        { text: 'Nachname', key: 'lastname' },
        { text: 'E-Mail', key: 'email' },
        { text: 'Erstellt am', key: 'created' },
        { text: 'Rolle', key: 'role' },
        { text: 'Aktion', key: 'action', sortable: false },
      ],
      users: [],
    };
  },
  created() {
    this.fetchUsers();
  },
  methods: {
    async fetchUsers() {
      try {
        this.loading = true;
        const response = await axios.get('http://localhost:8080/api/users/all');
        this.users = response.data;
      } catch (error) {
        console.error('Fehler beim Abrufen der Benutzer:', error);
      } finally {
        this.loading = false;
      }
    },
    addUser() {
      // Füge hier die Logik zum Hinzufügen eines neuen Benutzers hinzu
    },
    editUser(user) {
      // Füge hier die Logik zum Bearbeiten des ausgewählten Benutzers hinzu
    },
  },
};
</script>

<style scoped>
.elevation-1 {
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
</style>
