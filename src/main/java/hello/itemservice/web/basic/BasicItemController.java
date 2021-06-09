package hello.itemservice.web.basic;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
