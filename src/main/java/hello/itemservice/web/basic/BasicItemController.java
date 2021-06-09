package hello.itemservice.web.basic;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    // @PostMapping("/add")  // @ModelAttribute 까지 생략 가능.
    public String addItemV4(Item item){ // --> addItemV4는 PRG 패턴이 필요하다!
        itemRepository.save(item);
        return "basic/item";
    }

    // @PostMapping("/add")  //  PRG 패턴 적용! 새로고침 문제 해결.
    public String addItemV5(Item item){
        itemRepository.save(item);
        return "redirect:/basic/items/" + item.getId();
        // redirect -> HTTP 응답 헤더의 Location: http://localhost:8080/basic/items/3 (상세 화면 컨트롤러 호출)
    }

    @PostMapping("/add")  // RedirectAttributes
    public String addItemV6(Item item, RedirectAttributes redirectAttributes){
        Item savedItem = itemRepository.save(item);
        // redirect와 관련된 속성 넣기 -> URL 에 pathVariable 바인딩 가능  {itemId}에 치환된다.
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        // 저장이 '완료'되었다는 뜻으로 사용.
        redirectAttributes.addAttribute("status", true);

        return "redirect:/basic/items/{itemId}"; // URL에 못들어 간 나머지 속성들은 (status는) 쿼리 파라미터로 들어간다.
        // http://localhost:8080/basic/items/3?status=true
    }

    // 수정 화면 GET : 수정 화면에 기존 정보만 뿌려준다.
    @GetMapping("/{itemId}/edit")
    public String editFrom(@PathVariable Long itemId, Model model){
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/editForm";
    }

    // 수정 처리 POST : 레포지토리에 update
    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute("item") Item item){
        itemRepository.update(itemId, item);
        return "redirect:/basic/items/{itemId}"; // 상세 화면으로 Redirect 하여 URL 변경.
        // http status code 302 == redirect. 즉, 컨트롤러를 다시 호출하는 것.
    }

    /**
     PRG Post/Redirect/Get 패턴
     [문제]
     웹브라우저의 '새로고침' 기능은 가장 최근에 보낸 http요청을 서버에 다시 전송한다.
     '상품 등록' 버튼의 경우, POST 요청이다.
     '상품 등록' 후, 화면은 상세화면이 보인다. 그러나 이 상태에서 새로고침을 누르면, POST가 재요청 되어 같은 데이터가 계속 쌓인다.
     [해결법]
     상품 등록 후, POST가 다시 전송되지 않도록, Redirect로 GET요청 컨트롤러(상품 상세 화면 요청)를 타도록 하자.
     그러면, 새로고침을 눌러도 가장 최근 요청인 GET을 재요청한다.
     */

    @PostConstruct // 테스트용 데이터 추가
    public void init(){
        itemRepository.save(new Item("itemA", 10000, 10));
        itemRepository.save(new Item("itemB", 20000, 20));
    }
}
