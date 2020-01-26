<template>
    <div class="card">
        <b-loading :active.sync="loading"></b-loading>

        <card-header-with-provider-buttons label="Список вопросов" :redirect-to="redirectTo"></card-header-with-provider-buttons>

        <div class="card-content">
            <div class="content">
                <p>
                    Отправьте CSV файл чтобы создать новый набор вопросов
                </p>
                <form method="post" action="/question/sendCsv" enctype="multipart/form-data" >
                    <div class="notification" :class="notification.type" v-if="this.notification.text">
                        {{this.notification.text}}
                    </div>

                    <div class="field">
                        <label class="label">Csv</label>
                        <input class="input"
                               :class="{'is-danger': $v.email.$dirty, 'is-success':  $v.email.$dirty}"
                               name="file"
                               accept=".csv,.txt"
                               type="file"
                               placeholder="Csv file input"

                              >
                    </div>

                    <div class="field is-grouped">
                        <p class="control">
                            <button class="button is-success">Отправить
                            </button>
                        </p>

                    </div>
                </form>
            </div>
        </div>
    </div>
</template>

<script>
    import {mapActions} from 'vuex'
    import {required} from 'vuelidate/lib/validators'
    import CardHeaderWithProviderButtons from "../CardHeaderWithProviderButtons";

    export default {
        name: "QuestionPage",
        components: {CardHeaderWithProviderButtons},
        props: ['redirectTo', 'message'],
        data() {
            return {
                loading: false,
                email: "",
                rememberMe: false,
                notification: {
                    type: '',
                    text: ''
                }
            }
        },
        validations: {
            email: {
                required
            },
            password: {

            }
        },
        methods: {
            ...mapActions('user', ['setUser']),
            handleSubmit(e) {
                e.preventDefault()

                this.loading = true;
                this.$http.post('/signIn', {
                    email: this.email,
                    password: this.password,
                    rememberMe: this.rememberMe
                }).then(response => {
                    this.loading = false;
                    const userData = response.data
                    this.setUser(userData)
                    this.$emit('loggedIn', userData)
                    const nextTo = this.redirectTo ? this.redirectTo : '/profile'
                    this.$router.push(nextTo)
                }).catch(function (error) {
                    this.loading = false;
                    if (error.data && error.data.errorCode) {
                        this.$v.$reset();
                        this.notification.type = 'is-danger';
                        this.notification.text = this.getErrorMessage(error.data);
                    } else {
                        console.error(error);
                    }
                });

            },
            getErrorMessage(response) {
                if (response.errorCode === 'InvalidPassword') {
                    return `Wrong email or password. Your account will be temporary blocked after ${response.attemptsAllowed} try(s)`
                } else if (response.errorCode === 'NonActivatedUserEmail') {
                    return 'Email has not been confirmed yet'
                } else if (response.errorCode === 'UserNotFound') {
                    return 'There is no user by this email'
                } else if (response.errorCode === 'TooManyRequests') {
                    return `You reached attempts limit. Please try later at ${new Date(response.nextAllowedAttemptTime).toLocaleString()} or get in touch with site admin`
                } else {
                    return 'System error. Please get in touch with site admin to solve this error.'
                }
            }
        },
        created() {
            if (this.message === 'passwordChanged') {
                this.notification.type = 'is-success';
                this.notification.text = 'Password has been changed. Please use your email and new password to sign in'
            } else if (this.message === 'emailVerified') {
                this.notification.type = 'is-success';
                this.notification.text = 'Email has been verified successfully. Please use your email, password to sign in'
            } else if (this.message === 'sessionExpired') {
                this.notification.type = 'is-info';
                this.notification.text = 'Session is expired. Please sign in'
            } else if (this.message === 'activateEmail') {
                this.notification.type = 'is-info';
                this.notification.text = 'Please activate your account using the link we have sent to your email. You can sign in then.'
            } else if (this.message === 'authenticationRequired') {
                this.notification.type = 'is-warning';
                this.notification.text = 'Please sign in to get access to page'
            }
        }
    }
</script>

<style scoped>

</style>