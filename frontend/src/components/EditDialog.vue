<template>
    <v-card class="rounded-lg">
        <v-card-title>Edit User</v-card-title>

        <v-card-text>
            <v-text-body>Firstname</v-text-body>
            <v-text-field clearable class="mt-1" 
                :rules="[rules.required, rules.counter]"
                v-model="user.firstname" :counter="50" required variant="outlined"/>

            <v-text-body>Lastname</v-text-body>
            <v-text-field clearable class="mt-1" 
                :rules="[rules.required, rules.counter]"
                v-model="user.lastname" :counter="50" required variant="outlined"/>

            <v-text-body>Email</v-text-body>
            <v-text-field clearable class="mt-1" 
                :rules="[rules.required, rules.email]"
                v-model="user.email" required variant="outlined"/>

            <v-text-body>Role</v-text-body>
            <v-select clearable class="mt-1" 
                :rules="[rules.required, rules.roles]"
                v-model="selectedRoles" multiple
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
 
            <v-text-body>Password</v-text-body>
            <v-text-field class="mt-1" 
                :rules="[rules.required, rules.counter]"
                :type="show ? 'text' : 'password'" :append-inner-icon="show ? 'mdi-eye' : 'mdi-eye-off'"
                @click:append-inner="show = !show"
                v-model="user.password" required variant="outlined"
                />
        </v-card-text>

        <v-card-actions>
            <div class="d-flex w-100">
                <v-btn color="red" @click="cancelEdit(index)" variant="tonal">Cancel</v-btn>
                <v-btn color="green" @click="saveEditedUser(user)" variant="tonal" class="flex-grow-1">Save</v-btn>
            </div>
        </v-card-actions>
    </v-card>
</template>

<script setup>
    import axios from 'axios';
</script>

<script>
    export default {
        data() {
            return {
                user: {},
                selectedRoles: [],
                show: false,
                roleColors: {
                    Administrator: {background: '#e2ecf7', color: '#1c6ac1'},
                    Auditor: {background: '#e8f5e9', color: '#5fb762'},
                    Auditee: {background: '#dff6f9', color: '#00bcd4'},
                    Reporter: {background: '#fde7e5', color: '#f44336'},
                    Gast: {background: '#fff2df', color: '#ff9800'},
                    'Manual writer': {background: '#d1c4e9', color: '#7e57c2'}
                },
                roles: [
                    {id: 1, title: 'Administrator'},
                    {id: 2, title: 'Auditor'},
                    {id: 3, title: 'Auditee'},
                    {id: 4, title: 'Reporter'},
                    {id: 5, title: 'Gast'},
                    {id: 6, title: 'Manual writer'}
                ],
                rules: {
                    required: value => !!value || 'Required.',
                    counter: value => value.length <= 50 || 'Max 50 characters.',
                    email: value => {
                        const pattern = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
                        return pattern.test(value) || 'Invalid e-mail.'
                    },
                    roles: value => this.selectedRoles.length > 0 || 'At least one role must be selected.',
                },
            }
        },
        props: {
            item: {
                type: Object,
                required: true
            },
            index: {
                type: Number,
                required: true
            },
            email: {
                type: String,
                required: true
            },
        },
        mounted() {
            this.user = { ...this.item };
            this.selectedRoles = this.item.roles.map(userRoleName => {
                    let matchingRole = this.roles.find(role => role.title === userRoleName).title || null;
                    return matchingRole;
                }).filter(id => id !== null);
        },
        methods: {
            validateField(fieldValue, rules) {
                for (let rule of rules) {
                    const result = rule(fieldValue);
                    if (result !== true) {
                        return result;
                    }
                }
                return true;
            },
            async saveEditedUser() {
                const user = this.user;
                const rules = this.rules;
                
                const fieldsToValidate = [
                    { field: user.firstname, validators: [rules.required, rules.counter] },
                    { field: user.lastname, validators: [rules.required, rules.counter] },
                    { field: user.email, validators: [rules.required, rules.email] },
                    { field: user.password, validators: [rules.required, rules.counter] },
                    { field: this.selectedRoles, validators: [rules.required, rules.roles] }
                ];

                for (const fieldToValidate of fieldsToValidate) {
                    if (this.validateField(fieldToValidate.field, fieldToValidate.validators) !== true) {
                        return; // Exit early if validation fails for any field
                    }
                }

                const selectedRoleIds = this.selectedRoles.map(selectedRole => {
                    const matchingRole = this.roles.find(role => role.title === selectedRole);
                    return matchingRole ? matchingRole.id : null;
                }).filter(id => id !== null);

                user.roles = selectedRoleIds;

                try {
                    const response = await axios.put(`users/edit?email=${this.email}`, user);
                    if (response.status === 200) {
                        this.item.firstname = user.firstname;
                        this.item.lastname = user.lastname;
                        this.item.email = user.email;
                        this.item.roles = this.selectedRoles;
                        this.item.password = user.password;
                        this.$emit('cancel');
                    } else {
                        alert(response.data);
                    }
                } catch (error) {
                    alert(error.response.data);
                }
            },
            cancelEdit() {
                this.$emit('cancel'); //emit to parent to close the dialog
            },
            roleStyle(role) {
                const defaultStyle = {background: '#eeeeee', color: '#000000'};
                return this.roleColors[role.title] || defaultStyle;
            },
        }
    }
</script>