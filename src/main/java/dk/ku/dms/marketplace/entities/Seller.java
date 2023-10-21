package dk.ku.dms.marketplace.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class Seller
{
    @JsonProperty("id")
    public final int id;

    @JsonProperty("name")
    public final String name;

    @JsonProperty("company_name")
    public final String company_name;

    @JsonProperty("email")
    public final String email;

    @JsonProperty("phone")
    public final String phone;

    @JsonProperty("mobile_phone")
    public final String mobile_phone;

    @JsonProperty("cpf")
    public final String cpf;
    @JsonProperty("cnpj")
    public final String cnpj;

    @JsonProperty("address")
    public final String address;

    @JsonProperty("complement")
    public final String complement;

    @JsonProperty("city")
    public final String city;

    @JsonProperty("state")
    public final String state;

    @JsonProperty("zip_code")
    public final String zip_code;

    public Seller(@JsonProperty("id") int id, @JsonProperty("name") String name, @JsonProperty("company_name") String company_name,
                  @JsonProperty("email") String email, @JsonProperty("phone") String phone, @JsonProperty("mobile_phone") String mobile_phone,
                  @JsonProperty("cpf") String cpf, @JsonProperty("cnpj") String cnpj, @JsonProperty("address") String address,
                  @JsonProperty("complement") String complement, @JsonProperty("city") String city,
                  @JsonProperty("state") String state, @JsonProperty("zip_code") String zip_code) {
        this.id = id;
        this.name = name;
        this.company_name = company_name;
        this.email = email;
        this.phone = phone;
        this.mobile_phone = mobile_phone;
        this.cpf = cpf;
        this.cnpj = cnpj;
        this.address = address;
        this.complement = complement;
        this.city = city;
        this.state = state;
        this.zip_code = zip_code;
    }

    //    public long getId() {
//        return id;
//    }
//
//    public void setId(long id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public int getOrder_count() {
//        return order_count;
//    }
//
//    public void setOrder_count(int order_count) {
//        this.order_count = order_count;
//    }


    @Override
    public String toString() {
        return "Seller{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", company_name='" + company_name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", mobile_phone='" + mobile_phone + '\'' +
                ", cpf='" + cpf + '\'' +
                ", cnpj='" + cnpj + '\'' +
                ", address='" + address + '\'' +
                ", complement='" + complement + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zip_code='" + zip_code + '\'' +
                '}';
    }
}
