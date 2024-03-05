<template>
    <v-container class="d-flex align-center justify-center">
        <div style="width: 925px;">
            <button @click="back">
            <v-icon size="40">mdi-arrow-left</v-icon>
            </button>
            <div class="title">New User</div>
            <div class="subtitle">Create a new User</div>
            <v-form @submit-prevent="addUser">
                <v-row>
                    <v-col>
                        <label for="">Firstname*</label>
                        <v-text-field 
                        variant="outlined"
                        class="inputfield" required
                        :model-value="user.firstname" 
                        @update:model-value="newValue => user.firstname = newValue" inputType="text" />
                    </v-col>
                    <v-col>
                        <label for="">Lastname*</label>
                        <v-text-field 
                        variant="outlined"
                        class="inputfield"
                        :model-value="user.lastname" required
                        @update:model-value="newValue => user.lastname = newValue" inputType="text" />
                    </v-col>
                </v-row>

                <v-row>
                    <v-col>
                        <label for="">E-Mail-Address*</label>
                        <v-text-field 
                        variant="outlined"
                        class="inputfield" required
                        :model-value="user.email" 
                        @update:model-value="newValue => user.email = newValue" inputType="text" />
                    </v-col>
                    <v-col>
                        <label for="">Phone-Number</label>
                        <v-text-field 
                        variant="outlined"
                        class="inputfield" :model-value="user.phoneNumber" 
                        @update:model-value="newValue => user.phoneNumber = newValue"></v-text-field>
                    </v-col>
                </v-row>

                <v-row>
                    <v-col>
                        <label for="">Roles*</label>
                        <v-select
                            :items="roles"
                            item-title="name"
                            item-value="id"
                            v-model="selectedRoles"
                            bg-color="none"
                            multiple
                            variant="outlined"
                        >
                            <template v-slot:selection="{item, index}">
                                <v-chip v-if="index < 2">
                                    <span>{{ item.title }}</span>
                                </v-chip>
                                <span 
                                v-if="index === 2"
                                class="text-grey text-caption align-self-center"
                                >
                                    (+{{ selectedRoles.length - 2 }} others)
                                </span>
                            </template>
                        </v-select>
                    </v-col>
                    <v-col>
                        <label>Date of Birth</label>
                        <v-text-field readonly="true" variant="outlined" @click="openDatePicker" @focus="openDatePicker" v-model="formattedDate"/> 
                        <v-dialog class="date-picker" v-model="showDatePicker" persistent>
                            <v-container>
                                <v-row justify="space-around">
                                    <v-date-picker :max="maxDate" elevation="24" v-model="tempDate">
                                        <template v-slot:actions>
                                            <v-spacer></v-spacer>
                                            <v-btn text color="black" @click="closeDatePicker">Close</v-btn>
                                            <v-btn text color="black" @click="dateSelected">Save</v-btn>
                                        </template>
                                    </v-date-picker>
                                </v-row>
                            </v-container>
                        </v-dialog>
                    </v-col>
                </v-row>
                <v-row>
                    <v-col>
                        <label for="">Password*</label>
                        <v-text-field 
                        variant="outlined"
                        class="inputfield" 
                        required
                        :model-value="password" 
                        @update:model-value="newValue => password = newValue" inputType="secure"/>
                    </v-col>
                    <v-col>
                        <label for="">Confirm Password*</label>
                        <v-text-field 
                        variant="outlined"
                        class="inputfield"
                        :model-value="confirmpassword" required
                        @update:model-value="newValue => confirmpassword = newValue" inputType="secure"/>
                    </v-col>
                </v-row>
                <div v-if="invalidInput" class="error-message">{{ invalidInput }}</div>
                <v-row>
                    <v-col>
                        <div class="btn-div">
                            <v-btn @click="addUser" class="submit-btn">confirm</v-btn>
                        </div>
                    </v-col>
                </v-row>
            </v-form>
        </div>
    </v-container>
</template>

<script setup>
    import axios from 'axios';
</script>
  
<script>
    export default {
        data() {
            return {
                user: {},
                confirmpassword: '',
                password: '',
                invalidInput: '',
                roles: [],
                selectedRoles: [],
                showDatePicker: false,
                dob: new Date(),
                tempDate: this.defaultDate,
            }
        },
        async mounted() {
          
        },
        computed: {
            formattedDate() {
                return this.dob instanceof Date ? this.dob.toLocaleDateString() : '';
            },
            maxDate() {
                let today = new Date();
                var dd = today.getDate();
                var mm = today.getMonth()+1; 
                var yyyy = today.getFullYear();
                today = yyyy + '-' + mm + '-' + dd;
                return today;
            }
        },
        methods: {
            openDatePicker() {
                this.showDatePicker = true;
            },
            closeDatePicker() {
                this.showDatePicker = false;
            },
            dateSelected() {
                this.dob = this.tempDate;
                this.closeDatePicker();
            },
            back() {
                this.$router.push({ name: 'Home' });
            },
            async addUser() {
                this.invalidInput = '';
                let user = this.user;

                try {
                    if(user.firstname == null ||user.firstname == '' || user.lastname == null || user.lastname == '' ||
                       user.email == null || user.email == '' || this.selectedRoles.length == 0 || this.password == null || this.password == '' || 
                       this.confirmpassword == null || this.confirmpassword == '') {
                        this.invalidInput = 'Please fill out all required fields.';

                    } else if(!(/^[a-z.-]+@[a-z.-]+\.[a-z]+$/i.test(user.email))) {
                        this.invalidInput = 'Invalid e-mail.'

                    } else if(this.password != this.confirmpassword) {
                        this.invalidInput = 'Password mismatch.';
                    } else if(!(/^\d{7,15}$/.test(user.phoneNumber)))
                        this.invalidInput = 'Invalide phone-number'
                    else {
                        const formData = new URLSearchParams();

                        formData.append('firstname', user.firstname.charAt(0).toUpperCase() + user.firstname.slice(1));
                        formData.append('lastname', user.lastname.charAt(0).toUpperCase() + user.lastname.slice(1));
                        formData.append('email', user.email);
                        formData.append('passwordHash', this.password);
                        formData.append('phoneNumber', user.phoneNumber);

                        if(this.dob != new Date()) 
                            formData.append('birthdate', this.dob);
                        console.log(this.selectedRoles)
                        formData.append('roles', this.selectedRoles);
                        const priviledgeValue = this.selectedRoles.includes(1) ? [1] : [2];
                        formData.append('priviledgeRoles', priviledgeValue);

                        const response = await axios.post('users/add', formData, {
                            withCredentials: true
                        });

                        if (response.status === 200 && response.data) {
                            this.$router.push({name: 'Home'});
                        } else {
                           alert('An error occurred, pleaes try again.');
                        }
                    }
                } catch (error) {
                   alert(error)
                }
            },
            handleDateUpdate(newDate) {
                this.dob = newDate;
            }
        }
    };
</script>

<style scoped>
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
        color: red;
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

  