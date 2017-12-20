package com.redhat.chefapp.winepairing;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("/v1")
public class WinePairingController {

    public static final String UNKOWN_FOOD = "UNKOWN_FOOD";
    public static final String ERROR = "ERROR";
    public static final String SUCCESS = "SUCCESS";

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    private final AtomicLong pairingCounter = new AtomicLong();

    @RequestMapping("/hello")
    public String home() {
      return "Hello World!";
    }

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(counter.incrementAndGet(),
                            String.format(template, name));
    }

    @RequestMapping("/pairing")
    public WinePairingResponse pairing(@RequestParam(value="foodType", defaultValue=UNKOWN_FOOD) String foodType) {
        FoodType _foodType = null;
        ArrayList<WineType> types = new ArrayList<WineType>();

        try {
            _foodType = FoodType.valueOf(foodType);

            if (_foodType.equals(FoodType.UNKOWN_FOOD)) {
                return new WinePairingResponse (pairingCounter.incrementAndGet(), "ERROR", UNKOWN_FOOD, new WineType[0]);
            }

            switch (_foodType) {
                case FISH: {
                    types.add(WineType.DRY_WHITE);
                    types.add(WineType.ROSE);
                    break;
                }
                case RED_MEAT: {
                    types.add(WineType.BOLD_RED);
                    break;
                }
                case WHITE_MEAT: {
                    types.add(WineType.LIGHT_RED);
                    types.add(WineType.SPARKLING);
                    break;
                }
                default: {
                    types.add(WineType.ROSE);
                }
            }

            System.out.println("IN: " + foodType + " OUT: " + (WineType[]) types.toArray(new WineType[types.size()]));

            return new WinePairingResponse (pairingCounter.incrementAndGet(), SUCCESS, SUCCESS, (WineType[]) types.toArray(new WineType[types.size()]));
        } catch (Throwable e) {
            return new WinePairingResponse (pairingCounter.incrementAndGet(), ERROR, UNKOWN_FOOD, new WineType[0]);
        }
    }
}