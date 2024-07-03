package pw.pdm.pdmserver.model.dto;

public class UserDto {
    private final Long id;
    private final String name;
    private final String product;
    private final String creation;
    private final String email;

    public UserDto(Long id, String name, String product, String creation, String email) {
        this.id = id;
        this.name = name;
        this.product = product;
        this.creation = creation;
        this.email = email;
    }

    // Getters
    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getProduct() {
        return product;
    }
    public String getCreation() {
        return creation;
    }
    public String getEmail() {
        return email;
    }
}
