package hello.itemservice.domain.item;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;  // Assertions.assertThat(findItem).isEqualTo(savedItem);

public class ItemRepositoryTest {

    ItemRepository itemRepository = new ItemRepository();

    // 각 테스트가 끝날 때 마다 실행하기. 각 테스트에 영향이 없도록 레포지토리 삭제
    @AfterEach
    void afterEach(){
        itemRepository.clearStore();
    }

    @Test
    void save(){ // 저장된 값이 조회한 값과 같은가?
        //given
        Item item = new Item("itemA", 10000, 10);
        //when
        Item savedItem = itemRepository.save(item);
        //then
        Item findItem = itemRepository.findById(item.getId());
        // Assertions.assertThat(findItem).isEqualTo(savedItem);  --> on demand static import
        assertThat(findItem).isEqualTo(savedItem);
    }

    @Test
    void findAll(){ // 아이템 2개 만들기.
        //given
        Item item1 = new Item("item1", 10000, 10);
        Item item2 = new Item("item2", 20000, 20);

        itemRepository.save(item1);
        itemRepository.save(item2);
        //when
        List<Item> result = itemRepository.findAll();

        //then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(item1, item2);
    }
    @Test
    void updateItem(){
        //given
        Item item = new Item("item1", 10000, 10);

        Item savedItem = itemRepository.save(item); // 1개 저장하고 아이디 구해두기
        Long itemId = savedItem.getId();

        //when -> update 호출하는 게 핵심
        Item updateParam = new Item("item2", 20000, 30);
        itemRepository.update(itemId, updateParam); // 아이디로 조회하여 파라미터 정보로 수정

        //then
        Item findItem = itemRepository.findById(itemId); // 아이디로 조회하여

        // 파라미터로 수정이 제대로 됬는지 확인  
        assertThat(findItem.getItemName()).isEqualTo(updateParam.getItemName());
        assertThat(findItem.getPrice()).isEqualTo(updateParam.getPrice());
        assertThat(findItem.getQuantity()).isEqualTo(updateParam.getQuantity());

    }
}
