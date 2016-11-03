package com.oneapm.example;

import com.oneapm.example.coins.Coin;
import com.oneapm.example.products.Product;
import com.oneapm.example.products.Products;
import com.oneapm.example.state.StateMachine;
import com.oneapm.example.state.StateMachineFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VendingMachine implements VendingMachineService{

    static Map<String, Command> commands = new HashMap<>();

    @Override
    public void depositeCoins(Coin... coins) {
        machine.doTransition(EventType.DEPOSITCOIN,Event.valueOf(Stream.of(coins).map(c -> c.name).toArray(String[]::new)));
        machine.doTransition(EventType.DEPOSITED, null);
    }

    @Override
    public void depositeProducts(Product... products) {
        machine.doTransition(EventType.DEPOSITPRODUCT,Event.valueOf(Stream.of(products).map(p->p.getName()).toArray(String[]::new)));
        machine.doTransition(EventType.DEPOSITED, null);

    }

    enum Command {
        COMMAND_Q("Q",
                  EventType.INSERT,
                  new CoinsEvent("Q")),
        COMMAND_N("N",
                  EventType.INSERT,
                  new CoinsEvent("N")),
        COMMAND_D("D",
                  EventType.INSERT,
                  new CoinsEvent("D")),
        COMMAND_DOLLAR("DOLLAR",
                       EventType.INSERT,
                       new CoinsEvent("DOLLAR")),
        COMMAND_A("GET-A",
                  EventType.GET,
                  new ProductEvent("A")),
        COMMAND_B("GET-B",
                  EventType.GET,
                  new ProductEvent("B")),
        COMMAND_C("GET-C",
                  EventType.GET,
                  new ProductEvent("C")),
        COMMAND_RETURN("COIN-RETURN",
                       EventType.RETURNCOIN,
                       new ProductEvent("COIN-RETURN"));
        public EventType eventType;
        public Event event;
        public String name;

        Command(String name, EventType eventType, Event event) {
            this.name = name;
            this.eventType = eventType;
            this.event = event;
        }

        public void doTransition() {
            machine.doTransition(this.eventType,
                                 this.event);
        }
        static Command anyOf(String cmd) {
            return Stream.of(Command.values()).filter(
                    c -> c.name.equalsIgnoreCase(cmd)
            ).findFirst()
                         .get();
        }
    }

    enum State {
        NORMAL,
        COININSERTED,
        COINDEPOSITED,
        PRODUCTDEPOSITED;

    }


    enum EventType {
        GET("BUY"),
        INSERT("INSERT"),
        DEPOSITCOIN("DEPOSITCOIN"),
        DEPOSITPRODUCT("DEPOSITPRODUCT"),
        DEPOSITED("DEPOSITED"),
        RETURNCOIN("RETURNCOIN");

        String name;

        EventType(String name) {
            this.name = name;
        }

        static EventType valueOf(String... command) {
            for (String c : command) {
                if (c.startsWith("GET")) return GET;
                if (c.equals("INSERT")) return INSERT;

            }
            return null;
        }
    }

    static interface Event<T> {
        List<T> get();

        static Event<?> valueOf(String... command) {
            return Arrays.asList(command)
                         .stream()
                         .anyMatch(c -> Coin.isOne(c))
                   ? new CoinsEvent(Arrays.asList(command)
                                          .stream()
                                          .filter(coin -> Coin.isOne(coin)))
                   : (Arrays.asList(command)
                            .stream()
                            .anyMatch(p -> Products.isOne(p))
                      ? new ProductEvent(Arrays.asList(command)
                                               .stream()
                                               .filter(pro -> Products.isOne(pro)))
                      : null);

        }
    }

    static class CoinsEvent implements Event<Coin> {
        List<Coin> coins = new ArrayList<>();

        public CoinsEvent(Stream<String> cs) {
            coins = cs.map(coin -> Coin.valueOf(coin))
                      .collect(Collectors.toList());
        }

        public CoinsEvent(String coin) {
            this.coins.add(Coin.valueOf(coin));
        }

        @Override
        public List<Coin> get() {
            return coins;
        }
    }

    static class ProductEvent implements Event<Product> {
        List<Product> products = new ArrayList<>();

        public ProductEvent(Stream<String> ps) {
            products = ps.map(product -> Products.valueOf(product))
                         .collect(Collectors.toList());
        }

        public ProductEvent(String product) {
            this.products.add(Products.valueOf(product));
        }

        @Override
        public List<Product> get() {
            return products;
        }
    }

    static StateMachineFactory<VendingMachine, State, EventType, Event> factory = new StateMachineFactory<VendingMachine, State, EventType, Event>(State.NORMAL).addTransition(State.NORMAL,
                                                                                                                                                                               State.COININSERTED,
                                                                                                                                                                               EventType.INSERT,
                                                                                                                                                                               VendingMachine::insertCoin)
                                                                                                                                                                .addTransition(State.COININSERTED,
                                                                                                                                                                               State.COININSERTED,
                                                                                                                                                                               EventType.INSERT,
                                                                                                                                                                               VendingMachine::insertCoin)
                                                                                                                                                                .addTransition(State.NORMAL,
                                                                                                                                                                               State.COINDEPOSITED,
                                                                                                                                                                               EventType.DEPOSITCOIN,
                                                                                                                                                                               VendingMachine::depositCoin)
                                                                                                                                                                .addTransition(State.NORMAL,
                                                                                                                                                                               State.PRODUCTDEPOSITED,
                                                                                                                                                                               EventType.DEPOSITPRODUCT,
                                                                                                                                                                               VendingMachine::depositeProduct)
                                                                                                                                                                .addTransition(State.COINDEPOSITED,
                                                                                                                                                                               State.NORMAL,
                                                                                                                                                                               EventType.DEPOSITED)
                                                                                                                                                                .addTransition(State.PRODUCTDEPOSITED,
                                                                                                                                                                               State.NORMAL,
                                                                                                                                                                               EventType.DEPOSITED)
                                                                                                                                                                .addTransition(State.COININSERTED,
                                                                                                                                                                               State.NORMAL,
                                                                                                                                                                               EventType.GET,
                                                                                                                                                                               VendingMachine::getProduct)
                                                                                                                                                                .addTransition(State.COININSERTED,
                                                                                                                                                                               State.NORMAL,
                                                                                                                                                                               EventType.RETURNCOIN,
                                                                                                                                                                               VendingMachine::returnCoin)

                                                                                                                                                                .installTopology();

    MachineState machineState = new MachineState();

    public static VendingMachine instance=new VendingMachine();

    static StateMachine machine = factory.make(instance);

    public void depositCoin(Event event) {
        machineState.depositeCoins(event.get());
    }

    public void depositeProduct(Event event) {
        machineState.depositeProducts(event.get());
    }

    public void insertCoin(Event event) {
        machineState.insertCoins(event.get());
    }

    public void getProduct(Event event) {
        machineState.obtainProduct((Product) event.get()
                                                  .get(0));
    }

    public void returnCoin(Event event) {
        machineState.returnCoin();
    }

    public String process(String input) {
        String[] commands = input.split(",\\s*");
        Arrays.stream(commands)
              .forEach(command -> Command.anyOf(command)
                                         .doTransition());
        return machineState.output;
    }

}
