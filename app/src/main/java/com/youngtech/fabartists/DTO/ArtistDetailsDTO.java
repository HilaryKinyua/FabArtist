package com.youngtech.fabartists.DTO;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * Created by VARUN on 01/01/19.
 */
public class ArtistDetailsDTO implements Serializable {

    String id = "";
    String user_id = "";
    String name = "";
    String category_id = "";
    String description = "";
    String about_us = "";
    ArrayList<SkillsDTO> skills = new ArrayList<>();
    String image = "";
    String completion_rate = "";
    String created_at = "";
    String updated_at = "";
    String bio = "";
    String longitude = "75.8989044";
    String latitude = "22.7497853";
    String location = "";
    String video_url = "";
    String price = "";
    String booking_flag = "";
    String is_online = "";
    String gender = "";
    String preference = "";
    String update_profile = "";
    String category_name = "";
    String ava_rating = "";
    ArrayList<ProductDTO> products = new ArrayList<>();
    ArrayList<ReviewsDTO> reviews = new ArrayList<>();
    ArrayList<GalleryDTO> gallery = new ArrayList<>();
    ArrayList<QualificationsDTO> qualifications = new ArrayList<>();
    ArrayList<ArtistBookingDTO> artist_booking = new ArrayList<>();
    String earning = "";
    String jobDone = "";
    String totalJob = "";
    String completePercentages = "";
    String artist_commission_type = "";
    String commission_type = "";
    String flat_type = "";
    String currency_type = "";
    String live_lat = "";
    String live_long = "";
    String city = "";
    String country = "";
    String banner_image = "";

    public String getBanner_image() {
        return banner_image;
    }

    public void setBanner_image(String banner_image) {
        this.banner_image = banner_image;
    }

    public String getLive_lat() {
        return live_lat;
    }

    public void setLive_lat(String live_lat) {
        this.live_lat = live_lat;
    }

    public String getLive_long() {
        return live_long;
    }

    public void setLive_long(String live_long) {
        this.live_long = live_long;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAbout_us() {
        return about_us;
    }

    public void setAbout_us(String about_us) {
        this.about_us = about_us;
    }

    public ArrayList<SkillsDTO> getSkills() {
        return skills;
    }

    public void setSkills(ArrayList<SkillsDTO> skills) {
        this.skills = skills;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCompletion_rate() {
        return completion_rate;
    }

    public void setCompletion_rate(String completion_rate) {
        this.completion_rate = completion_rate;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getBooking_flag() {
        return booking_flag;
    }

    public void setBooking_flag(String booking_flag) {
        this.booking_flag = booking_flag;
    }

    public String getIs_online() {
        return is_online;
    }

    public void setIs_online(String is_online) {
        this.is_online = is_online;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPreference() {
        return preference;
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }

    public String getUpdate_profile() {
        return update_profile;
    }

    public void setUpdate_profile(String update_profile) {
        this.update_profile = update_profile;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getAva_rating() {
        return ava_rating;
    }

    public void setAva_rating(String ava_rating) {
        this.ava_rating = ava_rating;
    }

    public ArrayList<ProductDTO> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<ProductDTO> products) {
        this.products = products;
    }

    public ArrayList<ReviewsDTO> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<ReviewsDTO> reviews) {
        this.reviews = reviews;
    }

    public ArrayList<GalleryDTO> getGallery() {
        return gallery;
    }

    public void setGallery(ArrayList<GalleryDTO> gallery) {
        this.gallery = gallery;
    }

    public ArrayList<QualificationsDTO> getQualifications() {
        return qualifications;
    }

    public void setQualifications(ArrayList<QualificationsDTO> qualifications) {
        this.qualifications = qualifications;
    }

    public ArrayList<ArtistBookingDTO> getArtist_booking() {
        return artist_booking;
    }

    public void setArtist_booking(ArrayList<ArtistBookingDTO> artist_booking) {
        this.artist_booking = artist_booking;
    }

    public String getEarning() {
        return earning;
    }

    public void setEarning(String earning) {
        this.earning = earning;
    }

    public String getJobDone() {
        return jobDone;
    }

    public void setJobDone(String jobDone) {
        this.jobDone = jobDone;
    }

    public String getTotalJob() {
        return totalJob;
    }

    public void setTotalJob(String totalJob) {
        this.totalJob = totalJob;
    }

    public String getCompletePercentages() {
        return completePercentages;
    }

    public void setCompletePercentages(String completePercentages) {
        this.completePercentages = completePercentages;
    }

    public String getArtist_commission_type() {
        return artist_commission_type;
    }

    public void setArtist_commission_type(String artist_commission_type) {
        this.artist_commission_type = artist_commission_type;
    }

    public String getCommission_type() {
        return commission_type;
    }

    public void setCommission_type(String commission_type) {
        this.commission_type = commission_type;
    }

    public String getFlat_type() {
        return flat_type;
    }

    public void setFlat_type(String flat_type) {
        this.flat_type = flat_type;
    }

    public String getCurrency_type() {
        return currency_type;
    }

    public void setCurrency_type(String currency_type) {
        this.currency_type = currency_type;
    }
}
