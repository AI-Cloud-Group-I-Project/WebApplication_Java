package com.example.SalesForecast.domain.weather.dto;

public class WeatherDto {
    /** "YYYY-MM-DD" フォーマットの日付文字列 */
    private String date;
    /** 気温 */
    private Float temperature;
    /** 降水量 */
    private Float precipitation;
    /** 天気文言 */
    private String weather;

    /** Jackson 等のシリアライザ用デフォルトコンストラクタ */
    public WeatherDto() {
    }

    /**
     * テスト用／プログラムから直接インスタンス化するときに利用
     *
     * @param date          "YYYY-MM-DD" フォーマット
     * @param temperature   気温
     * @param precipitation 降水量
     * @param weather       天気文言
     */
    public WeatherDto(String date, Float temperature, Float precipitation, String weather) {
        this.date = date;
        this.temperature = temperature;
        this.precipitation = precipitation;
        this.weather = weather;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public Float getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(Float precipitation) {
        this.precipitation = precipitation;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    @Override
    public String toString() {
        return "WeatherDto{" +
                "date='" + date + '\'' +
                ", temperature=" + temperature +
                ", precipitation=" + precipitation +
                ", weather='" + weather + '\'' +
                '}';
    }

    public boolean hasError() {
        return weather != null && weather.contains("error");
    }
}
