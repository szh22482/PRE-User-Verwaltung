<template>
    <v-container class="login-form">
        <v-card elevation="0" width="400" class="card" style="{'margin': 'auto'}">
            <v-card-title class="ps-0">USERMANAGEMENT</v-card-title>
            <v-card-subtitle class="ps-0">sign in</v-card-subtitle>
            <v-card-subtitle class="ps-0 mb-5">to continue</v-card-subtitle>

            <v-form @submit.prevent="login">
                <v-text-body>Email address</v-text-body>
                <v-text-field class="mt-2" v-model="email" variant="outlined" :rules="[rules.required, rules.email]"
                ></v-text-field>

                <v-text-body>Password</v-text-body>
                <v-text-field class="mt-2" 
                v-model="password" variant="outlined" 
                :type="show ? 'text' : 'password'" :append-inner-icon="show ? 'mdi-eye' : 'mdi-eye-off'"
                :rules="[rules.required]"
                @click:append-inner="show = !show"
                ></v-text-field>
                <div class="error-message">{{ invalidLogin }}</div>
                <v-action class="btn-div">
                    <v-btn @click="login" class="submit-btn">login</v-btn>
                </v-action>
            </v-form>
        </v-card>
    </v-container>
</template>

<script setup>
    import axios from 'axios';
</script>

<script>
    export default {
        data() {
            return {
                email: '',
                password: '',
                invalidLogin: '',
                show: false,
                rules: {
                    required: value => !!value || 'Required.',
                    email: value => /.+@.+\..+/.test(value) || 'E-mail must be valid',
                }
            };
        },
        methods: {
            async login() {
              //do nothing until user input is valid  
              if(this.rules.email(this.email) !== true || 
                    this.rules.required(this.email) !== true || 
                    this.rules.required(this.password) !== true) {
                return;
              }


              try {
                this.invalidLogin = '';
                const response = await axios.get("users/login", {
                    params: {
                        email: this.email,
                        password: this.password
                    }
                });
                console.log(response)
                if(response.status === 200) {
                    this.$router.push('/home'); //redirect to home page
                } else {
                    this.invalidLogin = response.data; //display error message
                    console.log(response)
                }
              } catch (error) {
                this.invalidLogin = error.response.data; //display error message
              }
            } 
        }
    }
</script>

<style scoped>

    .login-form {
        vertical-align: middle;
        display: flex;
        align-items: center;
        justify-content: center;
        height: 100vh;
    }

    .btn-div {
        display: flex;
        justify-content: end;
        margin-bottom: 5px;
    }

    .submit-btn {
        color: white;
        background: black;
        border-radius: 20px;
        font-size: 10px;
    }

    .error-message {
        color: #b00020;
        font-size: 12px;
        margin-top: 5px;
        margin-bottom: 5px;
        height: 12px;
    }
</style>