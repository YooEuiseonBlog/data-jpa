package study.datajpa.repository;

public class UsernameOnlyDto {

    private final String username;

    public UsernameOnlyDto(String username) { // 파라미터명을 분석함.
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
