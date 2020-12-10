package com.siterwell.sdk.http.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/16.
 */

public class WeatherBeanResultsNow implements Serializable {

    private static final long serialVersionUID = 717335912958244128L;
    private String code;
    private String visibility;
    private String wind_direction;
    private String pressure;
    private String clouds;
    private String feels_like;
    private String dew_point;
    private String wind_scale;
    private String temperature;
    private String humidity;
    private String wind_direction_degree;
    private String wind_speed;
    private String text;


    public WeatherBeanResultsNow() {
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getVisibility() {
        return this.visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getWind_direction() {
        return this.wind_direction;
    }

    public void setWind_direction(String wind_direction) {
        this.wind_direction = wind_direction;
    }

    public String getPressure() {
        return this.pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getClouds() {
        return this.clouds;
    }

    public void setClouds(String clouds) {
        this.clouds = clouds;
    }

    public String getFeels_like() {
        return this.feels_like;
    }

    public void setFeels_like(String feels_like) {
        this.feels_like = feels_like;
    }

    public String getDew_point() {
        return this.dew_point;
    }

    public void setDew_point(String dew_point) {
        this.dew_point = dew_point;
    }

    public String getWind_scale() {
        return this.wind_scale;
    }

    public void setWind_scale(String wind_scale) {
        this.wind_scale = wind_scale;
    }

    public String getTemperature() {
        return this.temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return this.humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getWind_direction_degree() {
        return this.wind_direction_degree;
    }

    public void setWind_direction_degree(String wind_direction_degree) {
        this.wind_direction_degree = wind_direction_degree;
    }

    public String getWind_speed() {
        return this.wind_speed;
    }

    public void setWind_speed(String wind_speed) {
        this.wind_speed = wind_speed;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "WeatherBeanResultsNow{" +
                "code='" + code + '\'' +
                ", visibility='" + visibility + '\'' +
                ", wind_direction='" + wind_direction + '\'' +
                ", pressure='" + pressure + '\'' +
                ", clouds='" + clouds + '\'' +
                ", feels_like='" + feels_like + '\'' +
                ", dew_point='" + dew_point + '\'' +
                ", wind_scale='" + wind_scale + '\'' +
                ", temperature='" + temperature + '\'' +
                ", humidity='" + humidity + '\'' +
                ", wind_direction_degree='" + wind_direction_degree + '\'' +
                ", wind_speed='" + wind_speed + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
