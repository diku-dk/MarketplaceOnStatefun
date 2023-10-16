package dk.ku.dms.marketplace.common.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Seller
{
    @JsonProperty("id")
    public int id;
    @JsonProperty("name")
    public String name;
    @JsonProperty("company_name")
    public String company_name;
    @JsonProperty("email")
    public String email;
    @JsonProperty("phone")
    public String phone;
    @JsonProperty("mobile_phone")
    public String mobile_phone;
    @JsonProperty("cpf")
    public String cpf;
    @JsonProperty("cnpj")
    public String cnpj;
    @JsonProperty("address")
    public String address;
    @JsonProperty("complement")
    public String complement;
    @JsonProperty("city")
    public String city;
    @JsonProperty("state")
    public String state;
    @JsonProperty("zip_code")
    public String zip_code;

//    这个字段应该删除了
    @JsonProperty("order_count")
    public int order_count;

    public Seller() {
        this.id = 0;
        this.name = "";
        this.order_count = 0;
        this.company_name = "";
        this.email = "";
        this.phone = "";
        this.mobile_phone = "";
        this.cpf = "";
        this.cnpj = "";
        this.address = "";
        this.complement = "";
        this.city = "";
        this.state = "";
        this.zip_code = "";
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
//                ", order_count=" + order_count +
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
