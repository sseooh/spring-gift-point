package gift.option.application.command;

public record OptionUpdateCommand(
        Long id,
        String name,
        Integer quantity,
        Long productId
) {
    public OptionUpdateCommand(Long id, String name, Integer quantity) {
        this(id, name, quantity, null);
    }
}
