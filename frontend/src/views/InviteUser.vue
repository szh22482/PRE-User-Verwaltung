<template>
  <v-container class="d-flex align-center justify-center">
    <div style="width: 925px;">
      <button @click="back">
        <v-icon size="40">mdi-arrow-left</v-icon>
      </button>
      <div class="title">Invite User</div>
      <div class="subtitle">Invite a new User</div>
      <!--v-form ref="form" @submit-prevent="sendEmail">
        <label>Name</label>
        <input type="text" v-model="user_name" name="name" placeholder="Name">
        <label>Email</label>
        <input type="email" v-model="user_email" name="email" placeholder="Email">
        <label>Message</label>
        <textarea name="message" v-model="user_message" cols="30" rows="5" placeholder="Message"></textarea>
     &lt;!&ndash; new &ndash;&gt;
    <input type="submit" value="Send">
      </v-form>-->

      <v-form ref="form" @submit-prevent="sendEmail">
        <v-row>
          <v-col cols="12" sm="6">
            <label for="">Firstname</label>
            <br>
            <v-text-field
              variant="outlined"
              class="inputfield" required
              v-model="user.firstname"
              :rules="[rules.required, rules.counter]"
            />
          </v-col>
          <v-col cols="12" sm="6">
            <label for="">Lastname</label>
            <br>
            <v-text-field
              variant="outlined"
              class="inputfield"
              v-model="user.lastname" required
              :rules="[rules.required, rules.counter]"
            />
          </v-col>
        </v-row>

        <v-row>
          <v-col cols="12" sm="6">
            <label for="">E-Mail-Address</label>
            <br>
            <v-text-field
              variant="outlined"
              class="inputfield" required
              v-model="user.email"
              :rules="[rules.required, rules.email]"
            />
          </v-col>

          <v-col cols="12" sm="6">
            <label for="">Roles</label>
            <br>
            <v-select
              clearable
              class="mt-1"
              v-model="selectedRoles"
              multiple
              :items="roles"
              variant="outlined"
              required
            >
              <template v-slot:selection="{ item, index }">
                <v-chip v-if="index < 2" :style="roleStyle(item)">
                  <span>{{ item.title }}</span>
                </v-chip>
                <span v-if="index === 2" class="text-grey text-caption align-self-center">
                        (+{{ selectedRoles.length - 2 }} others)
                        </span>
              </template>
            </v-select>
          </v-col>
        </v-row>

        <v-row>
          <v-col>
        <label>Message</label>
            <br>
        <textarea variant="outlined" name="message"></textarea>
          </v-col>
        </v-row>

        <div v-if="invalidInput" class="error-message">{{ invalidInput }}</div>
        <v-row>
          <v-col>
            <div class="btn-div">
              <v-btn @click="sendEmail" class="submit-btn">confirm</v-btn>
            </div>
          </v-col>
        </v-row>
      </v-form>


    </div>
    </v-container>
</template>

<script>
import emailjs from '@emailjs/browser';
import axios from 'axios';

export default {
  name: 'ContactUs',
  data() {
    return {
      user: {
        firstname: '',
        lastname: '',
        email: '',
      },
      roles: [],
      selectedRoles: [],
      message: '',
      roles: [
        {id: 1, title: 'Administrator'},
        {id: 2, title: 'Auditor'},
        {id: 3, title: 'Auditee'},
        {id: 4, title: 'Reporter'},
        {id: 5, title: 'Gast'},
        {id: 6, title: 'Manual Writer'}
      ],
      roleColors: {
        Administrator: {background: '#e2ecf7', color: '#1c6ac1'},
        Auditor: {background: '#e8f5e9', color: '#5fb762'},
        Auditee: {background: '#dff6f9', color: '#00bcd4'},
        Reporter: {background: '#fde7e5', color: '#f44336'},
        Gast: {background: '#fff2df', color: '#ff9800'},
        'Manual Writer': {background: '#d1c4e9', color: '#7e57c2'}
      },
      rules: {
        required: value => !!value || 'Field Required.',
        counter: value => value.length <= 50 || 'Max 50 characters.',
        email: value => {
          const pattern = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
          return pattern.test(value) || 'Invalid e-mail.'
        },
        roles: value => this.selectedRoles.length > 0 || 'At least one role must be selected.',
      },
    }
  },
  async mounted() {

  },
  created() {
    this.user = {
      firstname: '',
      lastname: '',
      email: '',
    };
  },
  methods: {
    back() {
      this.$router.push({ name: 'Home' });
    },
    validateField(fieldValue, rules) {
      for (let rule of rules) {
        const result = rule(fieldValue);
        if (result !== true) {
          return result;
        }
      }
      return true;
    },
    async sendEmail() {
      let user = this.user;
      let rules = this.rules;

      const fieldsToValidate = [
        { field: user.firstname, validators: [rules.required, rules.counter] },
        { field: user.lastname, validators: [rules.required, rules.counter] },
        { field: user.email, validators: [rules.required, rules.email] },
        { field: this.selectedRoles, validators: [rules.required, rules.roles] }
      ];

      for (let field of fieldsToValidate) {
        const result = this.validateField(field.field, field.validators);
        if (result !== true) {
          this.invalidInput = result;
          return;
        }
      }

      const selectedRoleIds = this.selectedRoles.map(selectedRole => {
        const matchingRole = this.roles.find(role => role.title === selectedRole);
        return matchingRole ? matchingRole.id : null;
      }).filter(id => id !== null);

      user.roles = selectedRoleIds;

      try {
        const respones = await axios.post('/users/add', user);
        if (respones.status === 200) {
          this.$router.push({name: 'Home'});
        } else {
          this.invalidInput = 'Something went wrong. Please try again.';
        }
      } catch (error) {
        console.error(error);
      }

      const emailParams = {

      };
      emailjs.send('service_jq2ljl5', 'template_pb4hyjo', this.$refs.form, {
        publicKey: bw5WWGXHH9hfUJgkb,
      })
        .then(
          (response) => {
            console.log('SUCCESS!', response.status, response.text);
          },
          (error) => {
            console.log('FAILED...', error);
          },
        );



      // Reset form field
      this.user.firstname = ''
      this.user.lastname = ''
      this.user.email = ''
      this.message = ''
    },
    roleStyle(role) {
        const defaultStyle = {background: '#eeeeee', color: '#000000'};
        return this.roleColors[role.title] || defaultStyle;
    },
    },
  };
</script>

<style scoped>
* {box-sizing: border-box;}

.container {
  display: block;
  margin:auto;
  text-align: center;
  border-radius: 5px;
  background-color: #f2f2f2;
  padding: 20px;
  width: 50%;
}

label {
  float: left;
}

/*input[type=text], [type=email], textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box;
  margin-top: 6px;
  margin-bottom: 16px;
  resize: vertical;
}*/

input[type=submit] {
  background-color: #4CAF50;
  color: white;
  padding: 12px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

input[type=submit]:hover {
  background-color: #45a049;
}

.title {
  margin-top: 10px;
  color: black;
  font-size: 30px;
}

.subtitle {
  color: black;
  font-size: 20px;
  margin-top: -10px;
  margin-bottom: 40px;
}

.btn-div {
  display: flex;
  justify-content: end;
}

.select {
  border: #707070 1px solid;
  background: white !important;
  height: 50px;
  margin: 10px 0px;
  min-width: 300px;
  border-radius: 7px;
  padding-inline: 10px;
  padding-top: 5px;
  display: block;
}

.error-message {
  color: #b10423;
  font-size: 12px;
  margin-top: 5px;
}

.submit-btn {
  color: white;
  background: black;
  border-radius: 20px;
  font-size: 10px;
}
</style>
