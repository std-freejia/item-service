package hello.itemservice.web.basic;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;

@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor // lombok. final이 붙은 객체의 생성자를 만들어주는 역할.
public class BasicItemController {

    private final ItemRepository itemRepository;

    /*  @RequiredArgsConstructor :  final이 붙은 객체의 생성자를 만들어주는 역할.
    // @Autowired : 생성자 1개만 있으면 @Autowired 생략 가능
    public BasicItemController(ItemRepository itemRepository){  // 스프링빈 주입
        this.itemRepository = itemRepository;
    }
    */

    // 아이템 '목록' 출력하기
    @GetMapping
    public String items(Model model){
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items); // 뷰에 보여줄 데이터들
        return "basic/items"; //  뷰 논리 이름 (templates 하위에 타임리프 뷰)
    }
    @PostConstruct
    public void init(){ // 테스트용 데이터 추가
        itemRepository.save(new Item("itemA", 10000, 10));
        itemRepository.save(new Item("itemB", 20000, 20));
    }

    // 상품 상세
    @GetMapping("/{itemId}")
    public String item(@PathVariable Long itemId, Model model){ // @PathVariable == {itemId}
        Item item = itemRepository.findById(itemId); // itemId로 레포지토리에서 item 조회
        model.addAttribute("item", item); // 뷰에 랜더링할 모델에 item 객체 담기
        return "basic/item"; // 뷰 템플릿 호출
    }

    // 상품 등록 '뷰'만 보여주기
    @GetMapping("/add")
    public String addForm(){
        return "basic/addForm"; // 등록 뷰 호출
    }

    // 상품 등록 처리 : form 에 들어있는 정보를 저장. form 태그 내부의 name 속성이 식별 기준.
    //@PostMapping("/add")
    public String addItemV1(@RequestParam String itemName,
                       @RequestParam Integer price,
                       @RequestParam Integer quantity,
                       Model model){
        Item item = new Item();
        item.setItemName(itemName);
        item.setPrice(price);
        item.setQuantity(quantity);

        itemRepository.save(item);

        model.addAttribute("item", item);
        // 등록된 item을 상세 화면으로 보내서 model에 담은 데이터를 뿌려주자.

        return "basic/item"; // '상품 상세' 뷰 호출
    }

    //@PostMapping("/add")  // @ModelAttribute : 1.요청 파라미터 처리, 2.Model추가.
    public String addItemV2(@ModelAttribute("item") Item item, Model model){
        /*
        Item item = new Item();
        item.setItemName(itemName);
        item.setPrice(price);
        item.setQuantity(quantity);

        -->  위의 4줄이 @ModelAttribute("item") 으로 정리가능! ->  Item 객체 생성하고 요청 파라미터 값을 넣어줌.
        */
        itemRepository.save(item);
        // model.addAttribute("item", item);   // Model에 자동 추가 되므로 생략 가능. @ModelAttribute("item") 의 역할.

        return "basic/item"; // 등록된 item을 상세 화면으로 보내서 model에 담은 데이터를 뿌려주자.
    }

    //@PostMapping("/add")  // Model 생략
    public String addItemV3(@ModelAttribute("item") Item item){
        itemRepository.save(item);
        // Model에 자동 추가 되므로 생략 가능. @ModelAttribute("item") 의 역할.
        // 클래스 명이 Item 이라면, 첫 글자만 소문자로 바꿔서, "item"이름으로 model에 넣어준다.
        return "basic/item";
    }

    @PostMapping("/add")  // @ModelAttribute 생략
    public String addItemV4(Item item){
        itemRepository.save(item);
        return "basic/item";
    }

}
