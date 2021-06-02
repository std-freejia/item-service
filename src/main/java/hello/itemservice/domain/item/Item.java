package hello.itemservice.domain.item;

import lombok.Getter;
import lombok.Setter;

// @Data : 이것은 좀 위험하므로 주의하기. 지나치게 생성해주니까.
@Getter @Setter
public class Item {

    private Long id;
    private String itemName;
    private Integer price; // price 가 안들어갈 수 있으니까 기본형 int 아닌 객체형 으로 쓰자. 객체는 null 들어감.
    private Integer quantity;

    public Item(){
        // 기본 생성자
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }

}
