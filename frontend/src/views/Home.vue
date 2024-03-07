<template>
  <v-card-title class="ps-4 pb-0 mb-0">USERS</v-card-title>
  <v-card-subtitle class="ps-4 pt-0 mt-0">view and manage users</v-card-subtitle>

  <v-container fluid>
    <v-data-table
      class="rounded-lg"
      :items="users"
      :headers="dynamicHeaders"
    >
      <template v-slot:headers>
        <tr :style="{ 'backgroung-color': '#f9fafc' }">
          <th>User</th>
          <th v-if="!isSmallScreen">Created</th>
          <th v-if="!colapseRole">Roles</th>
          <th></th>
        </tr>
      </template>

      <template v-slot:item.fullname="{ item, index }">
        <v-dialog>
          <template v-slot:activator="{ props }">
            <div class="align-horizontally" v-bind="props">
              <profile-picture
                :firstname="item.firstname"
                :lastname="item.lastname"
                :colorNr="item.colorNumber"
              />
              <div>
                <span class="username collapsible-text">
                  {{ item.firstname }} {{ item.lastname }}
                </span>
                <span class="collapsible-text email">
                  {{ item.email }}
                </span>
              </div>
            </div>
          </template>

          <template v-slot:default="{ isActive }">
            <v-container class="d-flex justify-center align-center">
              <v-card width="800" class="rounded-lg">
                <v-card-text>
                  <v-row justify="space-between" pt="1">
                    <v-col cols="7">
                      <span
                        class="username text-truncate"
                        :style="{ 'padding-left': '20px' }">
                        {{ item.firstname }} {{ item.lastname }}
                      </span>
                    </v-col>

                    <v-col class="text-right" cols="5">
                      <v-icon color="grey-darken-4" @click="openInnerDialog(index)">
                        mdi-pencil
                      </v-icon>
                      <v-icon color="red-lighten-1" :style="{ 'margin-inline': '10px' }"
                      @click="openDeleteDialog(index)">
                        mdi-delete
                      </v-icon>
                      <v-dialog width="300" v-model="dialogs[index].showDeleteDialog">
                        <Confirmation :item="item" :index="index"
                        @delete="deleteUser(index)" @cancel="cancelDelete(index)"/>
                      </v-dialog>
                      <v-dialog width="700" v-model="dialogs[index].showInnerDialog">
                        <EditDialog :item="item" :index="index" :email="item.email" @cancel="cancelEdit(index)"/>
                      </v-dialog>
                    </v-col>
                  </v-row>

                  <v-divider class="my-4"></v-divider>

                  <v-row>
                    <v-col class="user-label">Firstname</v-col>
                    <v-col class="user-info"> {{ item.firstname }}</v-col>
                  </v-row>
                  <v-row>
                    <v-col class="user-label">Lastname</v-col>
                    <v-col class="user-info"> {{ item.lastname }}</v-col>
                  </v-row>
                  <v-row>
                    <v-col class="user-label">Email</v-col>
                    <v-col class="user-info text-truncate"> {{ item.email }}</v-col>
                  </v-row>
                  <v-row>
                    <v-col class="user-label">Created</v-col>
                    <v-col class="user-info">
                      {{ new Date(item.created).toLocaleDateString("de-DE") }}
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col class="user-label">Password</v-col>
                    <v-col class="user-info"> {{ item.password }}</v-col>
                  </v-row>
                  <v-row>
                    <v-col class="user-label"> Roles</v-col>
                    <v-col>
                      <v-chip
                        class="role"
                        v-for="(role, index) in item.roles"
                        :key="index"
                        :style="roleStyle(role)"
                      >
                        {{ role }}
                      </v-chip>
                    </v-col>
                  </v-row>
                </v-card-text>

                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn
                    text="Close"
                    @click="isActive.value = false"
                  ></v-btn>
                </v-card-actions>
              </v-card>
            </v-container>
          </template>
        </v-dialog>
      </template>

      <template v-slot:item.created="{ item }">
        {{ new Date(item.created).toLocaleDateString("de-DE") }}
      </template>

      <template v-slot:item.roles="{ item }">
        <v-dialog>
          <template v-slot:activator="{ props }">
            <div v-if="!colapseDate && item.roles && item.roles.length > 0">
              <v-chip
                class="role"
                v-for="(role, index) in item.roles"
                :key="index"
                :style="roleStyle(role)">
                {{ role }}
              </v-chip>
            </div>
            <div v-else>
              <v-chip
                class="role"
                :style="roleStyle(item.roles[0])"
                v-bind="props"
                v-if="!colapseRole"
              >
                {{ item.roles[0] }}&nbsp;
                <span v-if="item.roles.length > 1">(+{{ item.roles.length - 1 }})</span>
              </v-chip>
            </div>
          </template>

          <template v-slot:default="{ isActive }">
            <v-card class="rounded-lg">
              <v-card-text>
                  <span class="username">
                    {{ item.firstname }} {{ item.lastname }} <br>
                  </span>
                <span class="email">
                    {{ item.email }}
                  </span>

                <v-divider class="my-4"></v-divider>

                <v-chip
                  class="role"
                  v-for="(role, index) in item.roles"
                  :key="index"
                  :style="roleStyle(role)"
                >
                  {{ role }}
                </v-chip>
              </v-card-text>

              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn
                  text="Close"
                  @click="isActive.value = false"
                ></v-btn>
              </v-card-actions>
            </v-card>
          </template>
        </v-dialog>
      </template>

      <template v-slot:item.actions="{ item, index }">
              <v-menu
                    v-model="dialogs[index].showOuterDialog"
                    :close-on-content-click="false"
                    location="end"
                  >
                    <template v-slot:activator="{ props} ">
                      <button
                        class="btn-more"
                        @click="openOutderDialog(index)"
                        v-bind="props">
                        <v-icon v-bind="props">mdi-dots-vertical</v-icon>
                      </button>
                    </template>

                    <v-card class="rounded-lg">
                      <v-btn variant="text" prepend-icon="mdi-pencil" block class="edit-button" @click="openInnerDialog(index)">
                        Edit user
                      </v-btn>
                      <v-divider></v-divider>
                      <v-btn variant="text" prepend-icon="mdi-delete" block @click="openDeleteDialog(index)">
                        Delete user
                      </v-btn>
                    </v-card>
                  </v-menu>
                  <v-dialog width="300" v-model="dialogs[index].showDeleteDialog">
                   <Confirmation :item="item" :index="index"
                   @delete="deleteUser(index)" @cancel="cancelDelete(index)"/>
                  </v-dialog>
                  <v-dialog width="700" v-model="dialogs[index].showInnerDialog">
                    <EditDialog :item="item" :index="index" :email="item.email" @cancel="cancelEdit(index)"/>
                  </v-dialog>
            </template>

      <template v-slot:bottom>
      </template> <!-- removes the default footer -->
    </v-data-table>

    <div class="add-user-button">
      <v-btn
        fab
        dark
        color="black"
        icon="mdi-plus"
        size="large"
        @click="$router.push('/add')"
      >
      </v-btn>
    </div>
  </v-container>
</template>

<script>
import axios from "axios";
import NavBar from "@/components/NavBar.vue";
import ProfilePicture from "@/components/ProfilePicture.vue";
import EditDialog from "@/components/EditDialog.vue";
import Confirmation from "@/components/Confirmation.vue";

export default {
  components: {
    ProfilePicture,
    EditDialog,
    NavBar,
    Confirmation
  },
  data() {
    return {
      isSmallScreen: false,
      colapseDate: false,
      colapseRole: false,
      dialogs: [],
      users: [],
      roleColors: {
        Administrator: {background: '#e2ecf7', color: '#1c6ac1'},
        Auditor: {background: '#e8f5e9', color: '#5fb762'},
        Auditee: {background: '#dff6f9', color: '#00bcd4'},
        Reporter: {background: '#fde7e5', color: '#f44336'},
        Gast: {background: '#fff2df', color: '#ff9800'},
        'Manual writer': {background: '#d1c4e9', color: '#7e57c2'}
      }
    }
  },
  computed: {
    dynamicHeaders() {
      let headers = [
        {text: 'User', value: 'fullname', sortable: true},
        ...(!this.isSmallScreen ? [{text: 'Created', value: 'created', sortable: true}] : []),
        ...(!this.colapseRole ? [{text: 'Roles', value: 'roles', sortable: false}] : []),
        {text: '', value: 'actions', width: '30px', sortable: false},
      ];
      return headers;
    },
  },
  async mounted() {
    this.checkScreenSize();
    window.addEventListener('resize', this.checkScreenSize)
    try {
      const response = await axios.get("/users/all");
      if (response != null) {
        this.users = response.data;
        console.log(this.users)
        this.dialogs = this.users.map(() => ({ showOuterDialog: false, showInnerDialog: false, showDeleteDialog: false}));
      } else {
        alert("No data found");
      }
    } catch (e) {
      alert(e)
    }
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.checkScreenSize);
  },
  methods: {
    openOutderDialog(index) {
      this.dialogs[index].showOuterDialog = true;
    },
    openInnerDialog(index) {
      this.dialogs[index].showOuterDialog = false;
      this.dialogs[index].showInnerDialog = true;
    },
    openDeleteDialog(index) {
      this.dialogs[index].showOuterDialog = false;
      this.dialogs[index].showDeleteDialog = true;
    },
    closeDeleteDialog(index) {
      this.dialogs[index].showDeleteDialog = false;
    },
    closeInnerDialog(index) {
      this.dialogs[index].showInnerDialog = false;
    },
    checkScreenSize() {
      this.isSmallScreen = window.innerWidth < 960;
      this.colapseDate = window.innerWidth < 750;
      this.colapseRole = window.innerWidth < 560;
      this.marginWidth = this.isSmallScreen ? '0px' : '70px';
    },
    roleStyle(role) {
      const defaultStyle = {background: '#eeeeee', color: '#000000'};
      return this.roleColors[role] || defaultStyle;
    },
    cancelEdit(index) {
      this.dialogs[index].showInnerDialog = false;
    },
    cancelDelete(index) {
      this.dialogs[index].showDeleteDialog = false;
    },
    deleteUser(index){
      this.users.splice(index, 1);
      this.dialogs.splice(index, 1);
      this.closeDeleteDialog(index);
    }
  },
}
</script>

<style>
.role {
  margin-right: 5px;
  margin-block: 5px;
}

.align-horizontally {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  font-weight: bold;
  font-size: 16px;
}

.email {
  font-size: 14px;
  color: #707070;
}

.collapsible-text {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: block;
  max-width: 400px;
}

.user-info {
  font-weight: bold;
}

.user-label {
  font-weight: bold;
  color: #616161;
  margin-left: 20px;
}

.edit-button {
  display: flex;
  justify-content: start !important;
}

.add-user-button {
  position: fixed;
  bottom: 35px;
  right: 25px;
}
</style>
