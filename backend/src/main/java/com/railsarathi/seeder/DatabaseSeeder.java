package com.railsarathi.seeder;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.railsarathi.entity.Coach;
import com.railsarathi.entity.Seat;
import com.railsarathi.entity.Station;
import com.railsarathi.entity.Train;
import com.railsarathi.entity.TrainSchedule;
import com.railsarathi.enums.BerthType;
import com.railsarathi.enums.CoachClass;
import com.railsarathi.enums.SeatStatus;
import com.railsarathi.enums.TrainType;
import com.railsarathi.repository.CoachRepository;
import com.railsarathi.repository.SeatRepository;
import com.railsarathi.repository.StationRepository;
import com.railsarathi.repository.TrainRepository;
import com.railsarathi.repository.TrainScheduleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final StationRepository stationRepository;
    private final TrainRepository trainRepository;
    private final TrainScheduleRepository scheduleRepository;
    private final CoachRepository coachRepository;
    private final SeatRepository seatRepository;
    private final com.railsarathi.repository.UserRepository userRepository;
    private final com.railsarathi.repository.NotificationRepository notificationRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        seedUsersIfEmpty();
        seedNotificationsIfEmpty();

        if (stationRepository.count() > 0) {
            log.info("Database already seeded with railway stations and trains. Skipping seeder.");
            return;
        }

        log.info("Seeding authentic Indian Railways stations, flagship trains, schedules, and coaches...");

        // 1. Seed Major Stations
        Station hwh = saveStation("HWH", "Howrah Junction", "Kolkata", "West Bengal", "ER", 23);
        Station ndls = saveStation("NDLS", "New Delhi", "New Delhi", "Delhi", "NR", 16);
        Station csmt = saveStation("CSMT", "Chhatrapati Shivaji Maharaj Terminus", "Mumbai", "Maharashtra", "CR", 18);
        Station sbc = saveStation("SBC", "KSR Bengaluru City", "Bengaluru", "Karnataka", "SWR", 10);
        Station mas = saveStation("MAS", "Chennai Central", "Chennai", "Tamil Nadu", "SR", 17);
        Station njp = saveStation("NJP", "New Jalpaiguri Junction", "Siliguri", "West Bengal", "NFR", 5);
        Station cnb = saveStation("CNB", "Kanpur Central", "Kanpur", "Uttar Pradesh", "NCR", 10);
        Station pryj = saveStation("PRYJ", "Prayagraj Junction", "Prayagraj", "Uttar Pradesh", "NCR", 10);
        Station ddu = saveStation("DDU", "Pt. Deen Dayal Upadhyaya Junction", "Mughalsarai", "Uttar Pradesh", "ECR", 8);
        Station gaya = saveStation("GAYA", "Gaya Junction", "Gaya", "Bihar", "ECR", 9);
        Station dhn = saveStation("DHN", "Dhanbad Junction", "Dhanbad", "Jharkhand", "ECR", 8);
        Station asn = saveStation("ASN", "Asansol Junction", "Asansol", "West Bengal", "ER", 7);
        Station bpl = saveStation("BPL", "Bhopal Junction", "Bhopal", "Madhya Pradesh", "WCR", 6);
        Station adi = saveStation("ADI", "Ahmedabad Junction", "Ahmedabad", "Gujarat", "WR", 12);
        Station pune = saveStation("PUNE", "Pune Junction", "Pune", "Maharashtra", "CR", 6);
        Station bbs = saveStation("BBS", "Bhubaneswar", "Bhubaneswar", "Odisha", "ECoR", 6);

        // 2. Train 1: 22301 - Howrah to New Jalpaiguri Vande Bharat Express
        Train vandeBharat = Train.builder()
                .trainNumber("22301")
                .trainName("Howrah - NJP Vande Bharat Express")
                .trainType(TrainType.VANDE_BHARAT)
                .sourceStation(hwh)
                .destinationStation(njp)
                .runsOnDays("MON,TUE,THU,FRI,SAT,SUN")
                .totalDistanceKm(565.0)
                .totalDurationMinutes(450)
                .build();
        trainRepository.save(vandeBharat);

        saveSchedule(vandeBharat, hwh, 1, null, LocalTime.of(5, 55), 0, 0.0, 1, "Platform 8");
        saveSchedule(vandeBharat, asn, 2, LocalTime.of(7, 37), LocalTime.of(7, 39), 2, 200.0, 1, "Platform 4");
        saveSchedule(vandeBharat, njp, 3, LocalTime.of(13, 25), null, 0, 565.0, 1, "Platform 1");

        seedCoachesAndSeats(vandeBharat, List.of(
                CoachClass.EXECUTIVE_CHAIR_CAR,
                CoachClass.AC_CHAIR_CAR,
                CoachClass.AC_CHAIR_CAR
        ));

        // 3. Train 2: 12301 - Howrah to New Delhi Rajdhani Express (via Gaya, DDU, Prayagraj, Kanpur)
        Train rajdhani = Train.builder()
                .trainNumber("12301")
                .trainName("Howrah - New Delhi Rajdhani Express")
                .trainType(TrainType.RAJDHANI)
                .sourceStation(hwh)
                .destinationStation(ndls)
                .runsOnDays("MON,TUE,WED,THU,FRI,SAT,SUN")
                .totalDistanceKm(1451.0)
                .totalDurationMinutes(1035)
                .build();
        trainRepository.save(rajdhani);

        saveSchedule(rajdhani, hwh, 1, null, LocalTime.of(16, 50), 0, 0.0, 1, "Platform 9");
        saveSchedule(rajdhani, asn, 2, LocalTime.of(18, 57), LocalTime.of(19, 0), 3, 200.0, 1, "Platform 4");
        saveSchedule(rajdhani, dhn, 3, LocalTime.of(19, 55), LocalTime.of(20, 0), 5, 259.0, 1, "Platform 3");
        saveSchedule(rajdhani, gaya, 4, LocalTime.of(22, 19), LocalTime.of(22, 22), 3, 459.0, 1, "Platform 1");
        saveSchedule(rajdhani, ddu, 5, LocalTime.of(0, 45), LocalTime.of(0, 55), 10, 664.0, 2, "Platform 2");
        saveSchedule(rajdhani, pryj, 6, LocalTime.of(2, 43), LocalTime.of(2, 45), 2, 817.0, 2, "Platform 1");
        saveSchedule(rajdhani, cnb, 7, LocalTime.of(4, 50), LocalTime.of(4, 55), 5, 1011.0, 2, "Platform 1");
        saveSchedule(rajdhani, ndls, 8, LocalTime.of(10, 5), null, 0, 1451.0, 2, "Platform 16");

        seedCoachesAndSeats(rajdhani, List.of(
                CoachClass.FIRST_AC,
                CoachClass.SECOND_AC,
                CoachClass.THIRD_AC,
                CoachClass.THIRD_AC_ECONOMY
        ));

        // 4. Train 3: 12951 - Mumbai Central to New Delhi Tejas Rajdhani Express
        Train tejasRajdhani = Train.builder()
                .trainNumber("12951")
                .trainName("Mumbai - New Delhi Tejas Rajdhani")
                .trainType(TrainType.RAJDHANI)
                .sourceStation(csmt)
                .destinationStation(ndls)
                .runsOnDays("MON,TUE,WED,THU,FRI,SAT,SUN")
                .totalDistanceKm(1386.0)
                .totalDurationMinutes(940)
                .build();
        trainRepository.save(tejasRajdhani);

        saveSchedule(tejasRajdhani, csmt, 1, null, LocalTime.of(17, 0), 0, 0.0, 1, "Platform 1");
        saveSchedule(tejasRajdhani, adi, 2, LocalTime.of(22, 30), LocalTime.of(22, 40), 10, 490.0, 1, "Platform 3");
        saveSchedule(tejasRajdhani, cnb, 3, LocalTime.of(4, 15), LocalTime.of(4, 20), 5, 950.0, 2, "Platform 2");
        saveSchedule(tejasRajdhani, ndls, 4, LocalTime.of(8, 32), null, 0, 1386.0, 2, "Platform 3");

        seedCoachesAndSeats(tejasRajdhani, List.of(
                CoachClass.FIRST_AC,
                CoachClass.SECOND_AC,
                CoachClass.THIRD_AC
        ));

        // 5. Train 4: 12002 - New Delhi to Bhopal Shatabdi Express
        Train shatabdi = Train.builder()
                .trainNumber("12002")
                .trainName("New Delhi - Bhopal Shatabdi Express")
                .trainType(TrainType.SHATABDI)
                .sourceStation(ndls)
                .destinationStation(bpl)
                .runsOnDays("MON,TUE,WED,THU,FRI,SAT,SUN")
                .totalDistanceKm(707.0)
                .totalDurationMinutes(500)
                .build();
        trainRepository.save(shatabdi);

        saveSchedule(shatabdi, ndls, 1, null, LocalTime.of(6, 0), 0, 0.0, 1, "Platform 1");
        saveSchedule(shatabdi, cnb, 2, LocalTime.of(9, 30), LocalTime.of(9, 35), 5, 440.0, 1, "Platform 2");
        saveSchedule(shatabdi, bpl, 3, LocalTime.of(14, 20), null, 0, 707.0, 1, "Platform 1");

        seedCoachesAndSeats(shatabdi, List.of(
                CoachClass.EXECUTIVE_CHAIR_CAR,
                CoachClass.AC_CHAIR_CAR
        ));

        // 6. Train 5: 12245 - Howrah to SMVT Bengaluru Duronto Express
        Train duronto = Train.builder()
                .trainNumber("12245")
                .trainName("Howrah - SMVT Bengaluru Duronto")
                .trainType(TrainType.DURONTO)
                .sourceStation(hwh)
                .destinationStation(sbc)
                .runsOnDays("TUE,WED,FRI,SAT,SUN")
                .totalDistanceKm(1945.0)
                .totalDurationMinutes(1740)
                .build();
        trainRepository.save(duronto);

        saveSchedule(duronto, hwh, 1, null, LocalTime.of(10, 50), 0, 0.0, 1, "Platform 20");
        saveSchedule(duronto, bbs, 2, LocalTime.of(16, 20), LocalTime.of(16, 30), 10, 437.0, 1, "Platform 4");
        saveSchedule(duronto, mas, 3, LocalTime.of(0, 30), LocalTime.of(0, 45), 15, 1200.0, 2, "Platform 1");
        saveSchedule(duronto, sbc, 4, LocalTime.of(15, 50), null, 0, 1945.0, 2, "Platform 1");

        seedCoachesAndSeats(duronto, List.of(
                CoachClass.SECOND_AC,
                CoachClass.THIRD_AC,
                CoachClass.SLEEPER
        ));

        // 7. Train 6: 12626 - New Delhi to Kerala Express (via Chennai/Bhopal/Nagpur)
        Train keralaExp = Train.builder()
                .trainNumber("12626")
                .trainName("New Delhi - Thiruvananthapuram Kerala Express")
                .trainType(TrainType.SUPERFAST)
                .sourceStation(ndls)
                .destinationStation(mas)
                .runsOnDays("MON,TUE,WED,THU,FRI,SAT,SUN")
                .totalDistanceKm(2180.0)
                .totalDurationMinutes(2160)
                .build();
        trainRepository.save(keralaExp);

        saveSchedule(keralaExp, ndls, 1, null, LocalTime.of(20, 10), 0, 0.0, 1, "Platform 3");
        saveSchedule(keralaExp, bpl, 2, LocalTime.of(5, 20), LocalTime.of(5, 25), 5, 705.0, 2, "Platform 1");
        saveSchedule(keralaExp, mas, 3, LocalTime.of(4, 30), null, 0, 2180.0, 3, "Platform 5");

        seedCoachesAndSeats(keralaExp, List.of(
                CoachClass.SECOND_AC,
                CoachClass.THIRD_AC,
                CoachClass.SLEEPER
        ));

        log.info("Successfully initialized 16 stations and 6 flagship trains with full schedules and coaches!");
    }

    private Station saveStation(String code, String name, String city, String state, String zone, int platforms) {
        Station station = Station.builder()
                .stationCode(code)
                .stationName(name)
                .city(city)
                .state(state)
                .zone(zone)
                .totalPlatforms(platforms)
                .build();
        return stationRepository.save(station);
    }

    private void saveSchedule(Train train, Station station, int stopOrder, LocalTime arr, LocalTime dep, int halt, double dist, int day, String platform) {
        TrainSchedule schedule = TrainSchedule.builder()
                .train(train)
                .station(station)
                .stopOrder(stopOrder)
                .arrivalTime(arr)
                .departureTime(dep)
                .haltMinutes(halt)
                .distanceFromSourceKm(dist)
                .dayNumber(day)
                .platform(platform)
                .build();
        scheduleRepository.save(schedule);
    }

    private void seedCoachesAndSeats(Train train, List<CoachClass> classes) {
        int coachCounter = 1;
        for (CoachClass coachClass : classes) {
            String prefix = getCoachPrefix(coachClass);
            String coachNumber = prefix + coachCounter++;
            int seatCount = getSeatsPerCoach(coachClass);

            Coach coach = Coach.builder()
                    .train(train)
                    .coachNumber(coachNumber)
                    .coachClass(coachClass)
                    .totalSeats(seatCount)
                    .build();
            Coach savedCoach = coachRepository.save(coach);

            List<Seat> seats = new ArrayList<>();
            for (int s = 1; s <= Math.min(24, seatCount); s++) { // seed sample seats per coach
                BerthType berthType = calculateBerthType(coachClass, s);
                Seat seat = Seat.builder()
                        .coach(savedCoach)
                        .seatNumber(s)
                        .berthType(berthType)
                        .seatStatus(SeatStatus.AVAILABLE)
                        .build();
                seats.add(seat);
            }
            seatRepository.saveAll(seats);
        }
    }

    private String getCoachPrefix(CoachClass coachClass) {
        return switch (coachClass) {
            case FIRST_AC -> "H";
            case SECOND_AC -> "A";
            case THIRD_AC, THIRD_AC_ECONOMY -> "B";
            case AC_CHAIR_CAR -> "C";
            case EXECUTIVE_CHAIR_CAR -> "E";
            case SLEEPER -> "S";
            case SECOND_SITTING -> "D";
        };
    }

    private int getSeatsPerCoach(CoachClass coachClass) {
        return switch (coachClass) {
            case FIRST_AC -> 24;
            case SECOND_AC -> 48;
            case THIRD_AC -> 64;
            case THIRD_AC_ECONOMY -> 72;
            case AC_CHAIR_CAR -> 78;
            case EXECUTIVE_CHAIR_CAR -> 52;
            case SLEEPER -> 72;
            case SECOND_SITTING -> 108;
        };
    }

    private BerthType calculateBerthType(CoachClass coachClass, int seatNum) {
        if (coachClass == CoachClass.AC_CHAIR_CAR || coachClass == CoachClass.EXECUTIVE_CHAIR_CAR) {
            return (seatNum % 3 == 0) ? BerthType.WINDOW : BerthType.AISLE;
        }
        int mod = seatNum % 8;
        return switch (mod) {
            case 7 -> BerthType.SIDE_LOWER;
            default -> BerthType.SIDE_UPPER;
        };
    }

    private void seedUsersIfEmpty() {
        if (!userRepository.existsByEmail("aarav@railsarathi.com")) {
            com.railsarathi.entity.User passenger = com.railsarathi.entity.User.builder()
                    .fullName("Aarav Mehta")
                    .username("aarav")
                    .email("aarav@railsarathi.com")
                    .password(passwordEncoder.encode("password123"))
                    .phone("+919876543210")
                    .role(com.railsarathi.enums.Role.ROLE_PASSENGER)
                    .build();
            userRepository.save(passenger);
            log.info("Seeded demo passenger account: aarav@railsarathi.com");
        }

        if (!userRepository.existsByEmail("admin@railsarathi.com")) {
            com.railsarathi.entity.User admin = com.railsarathi.entity.User.builder()
                    .fullName("RailSarathi Admin")
                    .username("admin")
                    .email("admin@railsarathi.com")
                    .password(passwordEncoder.encode("admin@2026"))
                    .phone("+919876543211")
                    .role(com.railsarathi.enums.Role.ROLE_ADMIN)
                    .build();
            userRepository.save(admin);
            log.info("Seeded demo admin account: admin@railsarathi.com");
        }
    }

    private void seedNotificationsIfEmpty() {
        if (notificationRepository.count() == 0) {
            userRepository.findByEmail("aarav@railsarathi.com").ifPresent(user -> {
                log.info("Seeding initial notifications for passenger user [{}]...", user.getEmail());

                com.railsarathi.entity.Notification n1 = com.railsarathi.entity.Notification.builder()
                        .recipientUser(user)
                        .recipientEmail(user.getEmail())
                        .title("Booking Confirmed: PNR 842-1948291")
                        .message("Your journey on 22301 Vande Bharat Express (HWH -> NJP) is confirmed for Coach C2, Seat 42.")
                        .type(com.railsarathi.enums.NotificationType.BOOKING_CONFIRMATION)
                        .priority(com.railsarathi.enums.NotificationPriority.HIGH)
                        .channel(com.railsarathi.enums.NotificationChannel.IN_APP)
                        .status(com.railsarathi.enums.NotificationStatus.UNREAD)
                        .actionUrl("/dashboard")
                        .build();

                com.railsarathi.entity.Notification n2 = com.railsarathi.entity.Notification.builder()
                        .recipientUser(user)
                        .recipientEmail(user.getEmail())
                        .title("Live Tracking Update: Train 22301")
                        .message("TinyFish AI Live Tracker reports 22301 Vande Bharat is running on schedule approaching Bolpur.")
                        .type(com.railsarathi.enums.NotificationType.TRAIN_DELAY)
                        .priority(com.railsarathi.enums.NotificationPriority.NORMAL)
                        .channel(com.railsarathi.enums.NotificationChannel.IN_APP)
                        .status(com.railsarathi.enums.NotificationStatus.UNREAD)
                        .actionUrl("/train/22301")
                        .build();

                com.railsarathi.entity.Notification n3 = com.railsarathi.entity.Notification.builder()
                        .recipientUser(user)
                        .recipientEmail(user.getEmail())
                        .title("Welcome to RailSarathi")
                        .message("Explore authentic Indian Railways schedules, multi-stop search, and instant boarding passes.")
                        .type(com.railsarathi.enums.NotificationType.SYSTEM_ANNOUNCEMENT)
                        .priority(com.railsarathi.enums.NotificationPriority.LOW)
                        .channel(com.railsarathi.enums.NotificationChannel.IN_APP)
                        .status(com.railsarathi.enums.NotificationStatus.READ)
                        .readAt(java.time.LocalDateTime.now())
                        .actionUrl("/")
                        .build();

                notificationRepository.saveAll(java.util.List.of(n1, n2, n3));
            });
        }
    }
}
