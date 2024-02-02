package adventofcode;

import adventofcode.reader.PuzzleReader;

import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.LongStream;

public class Day5IfYouGiveASeedAFertilizer {

    private static Map<String, Class<?>> classes = new HashMap<>();
    private static Map<Class<?>, List<Correspondence>> maps = new HashMap<>();

    static {
        classes.put("soil", Soil.class);
        classes.put("fertilizer", Fertilizer.class);
        classes.put("water", Water.class);
        classes.put("light", Light.class);
        classes.put("temperature", Temperature.class);
        classes.put("humidity", Humidity.class);
        classes.put("location", Location.class);
    }

    public static void main(String[] args) {

        PuzzleReader puzzleReader = new PuzzleReader("if-you-give-a-seed-a-fertilizer.txt");

        Queue<String> lines = new LinkedList<>(puzzleReader.getLines());

        String seedsLine = Optional.ofNullable(lines.poll()).orElseThrow(() -> new RuntimeException("Seeds not found !"));

        Optional<String> head = Optional.ofNullable(lines.poll());

        String converter = "";
        List<Correspondence> map = new ArrayList<>();

        while (head.isPresent()) {
            String line = head.get();

            if (line.matches("\\w+-to-\\w+\\smap:.*")) {
                if (!converter.isBlank())
                    maps.put(classes.get(converter), new ArrayList<>(map));

                converter = line.replaceAll("\\w+-to-", "").replaceAll("\\smap:", "");
                map.clear();
            }
            else if (!line.isBlank()) {
                List<Long> correspondenceElements = Arrays.stream(line.split(" "))
                        .map(Long::parseLong)
                        .toList();

                map.add(new Correspondence(correspondenceElements.get(1), correspondenceElements.get(0),
                        correspondenceElements.get(2) - 1));
            }

            head = Optional.ofNullable(lines.poll());
        }

        maps.put(classes.get(converter), new ArrayList<>(map));

        // Part 1
        System.out.println(Arrays.stream(seedsLine.replace("seeds: ", "")
                        .split(" "))
                .map(Long::parseLong)
                .map(Seed::new)
                .min(new Comparator<Seed>() {
                    @Override
                    public int compare(Seed o1, Seed o2) {
                        return o1.getLocation().getId().compareTo(o2.getLocation().getId());
                    }
                })
                .orElseThrow(() -> new RuntimeException("No seed found !"))
                .getLocation().getId());

        // Part 2
        Matcher matcher = Pattern.compile("\\d+\\s\\d+").matcher(seedsLine.replace("seeds: ", ""));

        List<Callable<Long>> seedRanges = new ArrayList<>();

        while (matcher.find()) {
            String found = matcher.group();

            String[] startAndLength = found.split(" ");

            seedRanges.add(new SeedRange(Long.parseLong(startAndLength[0]), Long.parseLong(startAndLength[1])));
        }

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        try {
            Long minLocation = -1L;

            List<Future<Long>> futures = executorService.invokeAll(seedRanges);

            for (Future<Long> future: futures) {
                Long rangeMinLocation = future.get();

                System.out.println(rangeMinLocation);

                if (minLocation < 0L || minLocation > rangeMinLocation)
                    minLocation = rangeMinLocation;
            }
        } catch (InterruptedException | ExecutionException exception) {
            throw new RuntimeException("Problem with concurrency");
        } finally {
            executorService.shutdown();
        }
    }

    private static class SeedRange implements Callable<Long> {

        private Long start;

        private Long length;

        public SeedRange(Long start, Long length) {
            this.start = start;
            this.length = length;
        }

        public Long getMinLocation() {
            return LongStream.range(start, start + length)
                    .mapToObj(Seed::new)
                    .min(new Comparator<Seed>() {
                        @Override
                        public int compare(Seed o1, Seed o2) {
                            return o1.getLocation().getId().compareTo(o2.getLocation().getId());
                        }
                    })
                    .orElseThrow(() -> new RuntimeException("No seed found !"))
                    .getLocation().getId();
        }

        @Override
        public Long call() throws Exception {
            return getMinLocation();
        }
    }

    private static class Correspondence {

        private Long source;

        private Long destination;

        private Long rangeLength;

        public Correspondence(Long source, Long destination, Long rangeLength) {
            this.source = source;
            this.destination = destination;
            this.rangeLength = rangeLength;
        }

        public boolean isMapped(Long id) {
            return id >= source && id <= source + rangeLength;
        }

        public boolean isReverseMapped(Long id) {
            return id >= destination && id <= destination + rangeLength;
        }

        public Long mappedTo(Long id) {
            return id - source + destination;
        }

        public Long reverseMappedTo(Long id) {
            return id - destination + source;
        }
    }

    private static class Category {

        protected Long id;

        public Category(Long id) {
            this.id = convert(id);
        }

        public Long getId() {
            return id;
        }

        protected Long convert(Long mappingId) {
            if (this.getClass() == Seed.class)
                return mappingId;

            return maps.get(this.getClass())
                    .stream()
                    .filter(c -> c.isMapped(mappingId))
                    .map(c -> c.mappedTo(mappingId))
                    .findFirst()
                    .orElse(mappingId);
        }
    }

    private static class Seed extends Category {

        private Soil soil;

        public Seed(Long id) {
            super(id);

            this.soil = new Soil(id);
        }

        public Location getLocation() {
            return soil.getFertilizer()
                    .getWater()
                    .getLight()
                    .getTemperature()
                    .getHumidity()
                    .getLocation();
        }
    }

    private static class Soil extends Category {

        private Fertilizer fertilizer;

        public Soil(Long seed) {
            super(seed);

            this.fertilizer = new Fertilizer(id);
        }

        public Fertilizer getFertilizer() {
            return fertilizer;
        }
    }

    private static class Fertilizer extends Category {

        private Water water;

        public Fertilizer(Long soil) {
            super(soil);
            this.water = new Water(id);
        }

        public Water getWater() {
            return water;
        }
    }

    private static class Water extends Category {

        private Light light;

        public Water(Long fertilizer) {
            super(fertilizer);
            this.light = new Light(id);
        }

        public Light getLight() {
            return light;
        }
    }

    private static class Light extends Category {

        private Temperature temperature;

        public Light(Long water) {
            super(water);
            this.temperature = new Temperature(id);
        }

        public Temperature getTemperature() {
            return temperature;
        }
    }

    private static class Temperature extends Category {

        private Humidity humidity;

        public Temperature(Long light) {
            super(light);
            this.humidity = new Humidity(id);
        }

        public Humidity getHumidity() {
            return humidity;
        }
    }

    private static class Humidity extends Category {

        private Location location;

        public Humidity(Long temperature) {
            super(temperature);
            this.location = new Location(id);
        }

        public Location getLocation() {
            return location;
        }
    }

    private static class Location extends Category {

        public Location(Long humidity) {
            super(humidity);
        }
    }
}
