<template>
  <v-data-table
    :items="users"
    :headers="headers"
    >
    <template v-slot:item.profilepicture="{ item }">
      <profile-picture
        :firstname="item.firstname"
        :lastname="item.lastname"
        :color-nr="item.colorNr"
      />
    </template>
  </v-data-table>
</template>

<script>
import axios from "axios";
import ProfilePicture from "@/components/ProfilePicture.vue";
export default {
  components: {
    ProfilePicture
  },
  data() {
    return {
      headers: [
        {title: "Profile Picture", key: "profilepicture"},
        {title: "Firstname", key: "firstname"},
        {title: "Lastname", key: "lastname"},
        {title: "Email", key: "email" },
        {title: "Created", key: "created"},
        {title: "Roles", key: "roles"}
      ],
      users: [],
    }
  },
  async mounted() {
    try {
      const response = await axios.get("/users/all");
      if(response != null) {
        this.users = response.data;
      }
      else {
        alert("No data found");
      }
    } catch (e) {
      alert(e)
    }
  }
}
</script>
