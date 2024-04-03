<template>
    <v-card width="300" class="rounded-lg align-center">
        <v-card-text class="align-center text-center">
            <v-icon size="x-large">mdi-alert-box-outline</v-icon>
            <v-card-text class="mb-0 pb-0">
                Are you sure you want to delete the user?
            </v-card-text>
            <v-card-subtitle class="mt-0 pt-0">
                This will not permenantly delete the user!
            </v-card-subtitle>
        </v-card-text>

        <v-divider></v-divider>
        <v-card-actions>
            <v-btn class="rounded-xl bg-white me-2 px-4" variant="outlined"
            @click="cancelDelete(index)">Cancel</v-btn>
            <v-btn class="rounded-xl bg-black px-4" @click="deleteUser()">Delete</v-btn>
        </v-card-actions>
    </v-card>
</template>

<script setup>
    import axios from 'axios';
</script>

<script>
    export default {
        props: {
            item: {
                type: Object,
                required: true
            },
            index: {
                type: Number,
                required: true
            },
        },
        methods: {
            cancelDelete() {
                this.$emit('cancel'); //emit to parent to close the dialog
            },
            async deleteUser() {
                try {
                    const response = await axios.put(`users/delete/${this.item.email}`,{
                    });
                    if (response.status === 200) {
                        this.$emit('delete');
                    } else {
                        console.log(response);
                    alert('Error deleting user:', response.data);
                    }
                } catch (e) {
                    alert(e);
                }
            }
        }
    }
</script>