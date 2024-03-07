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
                        <label for="">Firstname</label>
                        <v-text-field 
                        variant="outlined"
                        class="inputfield" required
                        :model-value="user.firstname" 
                        :rules="[rules.required, rules.counter]"
                        @update:model-value="newValue => user.firstname = newValue" inputType="text" />
                    </v-col>
                    <v-col>
                        <label for="">Lastname</label>
                        <v-text-field 
                        variant="outlined"
                        class="inputfield"
                        :model-value="user.lastname" required
                        :rules="[rules.required, rules.counter]"
                        @update:model-value="newValue => user.lastname = newValue" inputType="text" />
                    </v-col>
                </v-row>

                <v-row>
                    <v-col>
                        <label for="">E-Mail-Address</label>
                        <v-text-field 
                        variant="outlined"
                        class="inputfield" required
                        :model-value="user.email" 
                        :rules="[rules.required, rules.email]"
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
                        <label for="">Roles</label>
                        <v-select clearable class="mt-1" 
                            v-model="selectedRoles" multiple
                            :rules="[rules.required, rules.roles]"
                            :items="roles" variant="outlined" required
                        >

                            <template v-slot:selection="{item, index}">
                                <v-chip v-if="index < 2"  :style="roleStyle(item)">
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
                        <label for="">Password</label>
                        <v-text-field 
                        variant="outlined"
                        class="inputfield" 
                        required
                        :model-value="user.password" 
                        type="password"
                        :rules="[rules.required, rules.counter]"
                        @update:model-value="newValue => user.password = newValue" inputType="secure"/>
                    </v-col>
                    <v-col>
                        <label for="">Confirm Password</label>
                        <v-text-field 
                        variant="outlined"
                        class="inputfield"
                        type="password"
                        :model-value="confirmpassword" required
                        :rules="[rules.required, rules.counter]"
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
                roles: [
                    {id: 1, title: 'Administrator'},
                    {id: 2, title: 'Auditor'},
                    {id: 3, title: 'Auditee'},
                    {id: 4, title: 'Reporter'},
                    {id: 5, title: 'Gast'},
                    {id: 6, title: 'Manual writer'}
                ],
                roleColors: {
                    Administrator: {background: '#e2ecf7', color: '#1c6ac1'},
                    Auditor: {background: '#e8f5e9', color: '#5fb762'},
                    Auditee: {background: '#dff6f9', color: '#00bcd4'},
                    Reporter: {background: '#fde7e5', color: '#f44336'},
                    Gast: {background: '#fff2df', color: '#ff9800'},
                    'Manual writer': {background: '#d1c4e9', color: '#7e57c2'}
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
            validateField(fieldValue, rules) {
                for (let rule of rules) {
                    const result = rule(fieldValue);
                    if (result !== true) {
                        return result;
                    }
                }
                return true;
            },
            async addUser() {
                let user = this.user;
                let rules = this.rules;

                const fieldsToValidate = [
                    { field: user.firstname, validators: [rules.required, rules.counter] },
                    { field: user.lastname, validators: [rules.required, rules.counter] },
                    { field: user.email, validators: [rules.required, rules.email] },
                    { field: user.password, validators: [rules.required, rules.counter] },
                    { field: this.selectedRoles, validators: [rules.required, rules.roles] }
                ];

                for (let field of fieldsToValidate) {
                    const result = this.validateField(field.field, field.validators);
                    if (result !== true) {
                        this.invalidInput = result;
                        return;
                    }
                }

                if (user.password !== this.confirmpassword) {
                    console.log(user.password, this.confirmpassword)
                    this.invalidInput = 'Passwords do not match.';
                    return;
                }

                const selectedRoleIds = this.selectedRoles.map(selectedRole => {
                    const matchingRole = this.roles.find(role => role.title === selectedRole);
                    return matchingRole ? matchingRole.id : null;
                }).filter(id => id !== null);

                user.roles = selectedRoleIds;

                try {
                    const respones = await axios.post('/users/add', user);
                    if(respones.status === 200) {
                        this.$router.push({ name: 'Home' });
                    } else {
                        this.invalidInput = 'Something went wrong. Please try again.';
                    }
                } catch (error) {
                    console.error(error);
                }


                    
            },
            handleDateUpdate(newDate) {
                this.dob = newDate;
            },
            roleStyle(role) {
                const defaultStyle = {background: '#eeeeee', color: '#000000'};
                return this.roleColors[role.title] || defaultStyle;
            },
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