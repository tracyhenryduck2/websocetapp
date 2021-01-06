package me.siter.sdk.http.bean;

import java.io.Serializable;

/*
@class JWTBean
@autor Administrator
@time 2017/10/16 14:25
@email xuejunju_4595@qq.com
*/
public class JWTBean implements Serializable {

    private static final long serialVersionUID = -2125453657597278408L;
    /**
     * access_token : eyJhbGciOiJSUzI1NiJ9.eyJ1aWQiOiIxNjE5Mzc1MTc0MiIsImlzb2xhdGlvbiI6IjAwMDAwMDAwMDAwIiwiZXhwIjoxNDYxMjEzODUzLCJ0eXBlIjoiV0VCIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIl0sImp0aSI6IjdlZTVhZGUyLTNkNmItNDU4MS05M2I2LWZlYTUyNjMzNzc0MiJ9.BrWcVY82dmgdY2yM5l-6yYMgHm-VEyE1T_hAhwvz7x2mGpMfWxKTVsvjPYqfAiO79yhakW_-HzvmmCtBCKeTnw
     * refresh_token : eyJhbGciOiJSUzI1NiJ9.eyJ1aWQiOiIxNjE5Mzc1MTc0MiIsImF0aSI6IjdlZTVhZGUyLTNkNmItNDU4MS05M2I2LWZlYTUyNjMzNzc0MiIsImlzb2xhdGlvbiI6IjAwMDAwMDAwMDAwIiwiZXhwIjoxNDYxMjEzODUzLCJ0eXBlIjoiV0VCIiwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIl0sImp0aSI6IjNiOTVmMzkyLWVhMjQtNDIzZC1iMTlkLWZjMmQ2NDVmOGU4MiJ9.jI-6nNVpPTXIaguefTvW_boV5aq77Plobucukw72TNPd9PNHyQwuTl35Mvj9KS1irTOhbdUbj8yISobYQVBuiQ
     * token_type : bearer
     * expires_in : 86399
     * jti : 7ee5ade2-3d6b-4581-93b6-fea526337742
     */

    private String accessToken;
    private String refreshToken;
    private long expiresIn;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }


    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expires_in) {
        this.expiresIn = expiresIn;
    }


    public JWTBean(String accessToken, String refreshToken, long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

    public JWTBean() {
    }

    @Override
    public String toString() {
        return "JWTBean{" +
                "accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", expiresIn=" + expiresIn +
                '}';
    }
    
}
