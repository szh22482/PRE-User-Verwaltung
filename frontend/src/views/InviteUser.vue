<template>
  <v-container class="d-flex align-center justify-center">
    <div style="width: 925px;">
      <button @click="back">
        <v-icon size="40">mdi-arrow-left</v-icon>
      </button>
      <div class="title">Invite User</div>
      <div class="subtitle">Invite a new User</div>
      <v-form ref="form" @submit-prevent="sendEmail">
        <label>Name</label>
        <input type="text" v-model="user_name" name="name" placeholder="Name">
        <label>Email</label>
        <input type="email" v-model="user_email" name="email" placeholder="Email">
        <label>Message</label>
        <textarea name="message" v-model="user_message" cols="30" rows="5" placeholder="Message"></textarea>
     <!-- new -->
    <input type="submit" value="Send">
      </v-form>
    </div>
    </v-container>
</template>

<script>
import emailjs from '@emailjs/browser';

export default {
  name: 'ContactUs',
  data() {
    return {
      user_name: '',
      user_email: '',
      user_message: '',
    }
  },
  methods: {
    sendEmail() {
      const emailParams = {
        user_name: this.user_name,
        user_email: this.user_email,
        message: this.user_message
      };
        emailjs.send('service_jq2ljl5', 'template_pb4hyjo', emailParams)
          .then(
          (response) => {
            console.log('SUCCESS!', response.status, response.text);
          },
          (error) => {
            console.log('FAILED...', error);
          },
        );
        // Reset form field
        this.user_name = ''
        this.user_email = ''
        this.user_message = ''
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
