package hello.itemservice.domain.item;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository // 내부에 @Component 있으므로 컴포넌트 스캔의 대상이 된다.
public class ItemRepository {

    // 실무에서는 HashMap 쓰지 말기
    // 왜냐하면 여러 프로세스가 하나의 해시맵에 접근하게 되기 떄문에 안된다. 대신, ConcurrentHashMap 을 써야 함.
    private static final Map<Long, Item> store = new HashMap<>(); //static 사용한 것 주의하기.
    private static long sequence = 0L; //static 주의점 : 여러 프로세스가 동시에 접근하면 값이 꼬일 수 있음.

    public Item save(Item item){ // Item을 저장.
        item.setId(++sequence);
        store.put(item.getId(), item);
        return item;
    }

    public Item findById(Long id){
        return store.get(id);
    }

    public List<Item> findAll(){ // 전체 조회
        return new ArrayList<>(store.values());
    }

    // 수정
    public void update(Long itemId, Item updateParam){ // 아이디, 업데이트 할 파라미터.
        Item findItem = findById(itemId); // 수정할 아이템을 찾는다
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
    }

    public void clearStore(){
        store.clear();
    }
}
