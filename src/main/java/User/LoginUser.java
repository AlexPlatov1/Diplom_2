package User;

import org.apache.commons.lang3.RandomStringUtils;

public class LoginUser {

    private String email;
    private String password;

    public LoginUser(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static LoginUser from(LoginUser user) {
        return new LoginUser(user.email, user.password);
    }

    public static LoginUser getUserCredentialsWithInvalidLogin(LoginUser user) {
        user.setEmail(RandomStringUtils.randomAlphabetic(10));
        return new LoginUser(user.email, user.password);
    }

    public static LoginUser getUserCredentialsWithInvalidPassword(LoginUser user) {
        user.setPassword(RandomStringUtils.randomAlphabetic(10));
        return new LoginUser(user.email, user.password);
    }

    public static LoginUser getUserCredentialsWithoutPassword(LoginUser user) {
        return new LoginUser(user.email, "");
    }

    public static LoginUser getUserCredentialsWithoutLogin(LoginUser user) {
        return new LoginUser("", user.password);
    }
}