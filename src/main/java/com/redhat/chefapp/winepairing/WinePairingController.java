package com.redhat.chefapp.winepairing;

import java.util.List;
import java.util.Optional;
import io.opentracing.Span;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class WinePairingController {
    @Autowired
    private io.opentracing.Tracer tracer;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private CustomerRepository repository;

    @Autowired
    private WineRepository wineRepository;
    
    public static final String UNKOWN = "UNKOWN";
    public static final String UNKOWN_ERROR = "UNKOWN_ERROR";

    public static final String UNKOWN_WINE_TYPE = "UNKOWN_WINE_TYPE";
    public static final String UNKOWN_REGION = "UNKOWN_REGION";

    public static final String UNKOWN_FOOD = "UNKOWN_FOOD";
    public static final String ERROR = "ERROR";
    public static final String SUCCESS = "SUCCESS";

    private static final String template = "Hello, %s!";

    private final AtomicLong counter = new AtomicLong();
    private final AtomicLong pairingCounter = new AtomicLong();
    private final AtomicLong wineRepositoryCounter = new AtomicLong();

    @RequestMapping("/init")
    public String init () {
        Optional.ofNullable(tracer.activeSpan()).ifPresent(as -> as.setBaggageItem("wine-task", "init"));

        wineRepository.deleteAll();

        // save a couple of wines
		wineRepository.save(new Wine(WineType.DRY_WHITE, 2016, "Bodegas Terras Gauda 2016", "ALBARIÑO", "Bodegas Terras Gauda", "Spain", "70% Albariño, 18% Caiño y 12% Loureiro", "Golden", "Clear, expresive", "Acid, strong, fruity", "12.5%"));
		wineRepository.save(new Wine(WineType.BOLD_RED, 2013, "Sierra Cantabria Cuvee 2013", "RIOJA", "Bodegas y Viñedos Sierra Cantabria", "Spain", "100% Tempranillo", "Cherry red", "Elegant, intense", "Balanced, cocoa and red fruits", "14%"));


        repository.deleteAll();

		// save a couple of customers
		repository.save(new Customer("Alice", "Smith"));
		repository.save(new Customer("Bob", "Smith"));

		// fetch all customers
		System.out.println("Customers found with findAll():");
		System.out.println("-------------------------------");
		for (Customer customer : repository.findAll()) {
			System.out.println(customer);
		}
		System.out.println();

		// fetch an individual customer
		System.out.println("Customer found with findByFirstName('Alice'):");
		System.out.println("--------------------------------");
		System.out.println(repository.findByFirstName("Alice"));

		System.out.println("Customers found with findByLastName('Smith'):");
		System.out.println("--------------------------------");
		for (Customer customer : repository.findByLastName("Smith")) {
			System.out.println(customer);
        }
        
		return "The End";
    }

    /**
     * Wine search by wineType (DRY_WHITE, BOLD_RED) and region (RIOJA or ALBARIÑO)
     */
    @RequestMapping("/wine")
    public WineRepositoryResponse wine (@RequestParam(value="wineType", defaultValue=UNKOWN) String wineType, 
                                        @RequestParam(value="region", defaultValue=UNKOWN_REGION) String region) 
                                        throws InterruptedException {
        Optional.ofNullable(tracer.activeSpan()).ifPresent(as -> as.setBaggageItem("wine-task", "search"));

        Thread.sleep(1 + (long)(Math.random()*500));
        if (Math.random() > 0.8) { 
            throw new RuntimeException("Unknown freaking error!");
        }

        try {
            WineType _wineType = WineType.valueOf(wineType.toUpperCase());
            List<Wine> wines = wineRepository.findByTypeAndRegion(_wineType, region.toUpperCase());

            if (_wineType.equals(WineType.UNKOWN)) {
                return new WineRepositoryResponse (wineRepositoryCounter.incrementAndGet(), "ERROR", UNKOWN_WINE_TYPE, new Wine[0]);
            }

            Optional.ofNullable(tracer.activeSpan()).ifPresent(as -> as.setBaggageItem("wine-type", wineType));
            Optional.ofNullable(tracer.activeSpan()).ifPresent(as -> as.setBaggageItem("wine-region", region));
            System.out.println("IN: " + wineType + " OUT: " + Arrays.toString((Wine[]) wines.toArray(new Wine[wines.size()])));

            return new WineRepositoryResponse (wineRepositoryCounter.incrementAndGet(), SUCCESS, SUCCESS, (Wine[]) wines.toArray(new Wine[wines.size()]));
        } catch (Throwable e) {
            return new WineRepositoryResponse (wineRepositoryCounter.incrementAndGet(), ERROR, UNKOWN_ERROR, new Wine[0]);
        }
    }

    @RequestMapping("/pairing")
    public WinePairingResponse pairing(@RequestParam(value="foodType", defaultValue=UNKOWN_FOOD) String foodType) {
        Optional.ofNullable(tracer.activeSpan()).ifPresent(as -> as.setBaggageItem("wine-task", "pairing"));
        FoodType _foodType = null;
        ArrayList<WineType> types = new ArrayList<WineType>();

        try {
            _foodType = FoodType.valueOf(foodType);

            if (_foodType.equals(FoodType.UNKOWN_FOOD)) {
                return new WinePairingResponse (pairingCounter.incrementAndGet(), "ERROR", UNKOWN_FOOD, new WineType[0]);
            }

            Optional.ofNullable(tracer.activeSpan()).ifPresent(as -> as.setBaggageItem("food-type", foodType));

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
                    types.add(WineType.MEDIUM_RED);
                }
            }

            System.out.println("IN: " + foodType + " OUT: " + (WineType[]) types.toArray(new WineType[types.size()]));

            return new WinePairingResponse (pairingCounter.incrementAndGet(), SUCCESS, SUCCESS, (WineType[]) types.toArray(new WineType[types.size()]));
        } catch (Throwable e) {
            return new WinePairingResponse (pairingCounter.incrementAndGet(), ERROR, UNKOWN_FOOD, new WineType[0]);
        }
    }

    /**  Tracing tests  **/

    @RequestMapping("/chaining")
    public String chaining() {
        Optional.ofNullable(tracer.activeSpan()).ifPresent(as -> as.setBaggageItem("chaining-task", "chain-value"));
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8080/hello", String.class);
        return "Chaining " + response.getBody();
    }
    
    @RequestMapping("/hello")
    public String hello() throws InterruptedException {
            Span parent = tracer
                .buildSpan("hello-task")
                .startManual();

            Thread.sleep(1 + (long)(Math.random()*500));
            try {
                Span child = tracer
                    .buildSpan("world-task")
                    .asChildOf(parent)
                    .startManual();
                Thread.sleep(1 + (long)(Math.random()*500));
                child.finish();
            } finally {
                parent.finish();
            }

        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8080/greeting", String.class);
        return "Hello World! " + response.getBody();
    }

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(counter.incrementAndGet(),
                            String.format(template, name));
    }
}