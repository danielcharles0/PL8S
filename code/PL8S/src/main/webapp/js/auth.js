// check that the token is still valid
if(tokenExpired()){
    // user not logged in, send him to the login page and then make him come back
    goToLogin();
}