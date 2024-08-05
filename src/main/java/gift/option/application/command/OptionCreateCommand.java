package gift.option.application.command;

import gift.option.domain.Option;

public record OptionCreateCommand(
        String name,
        Integer quantity,
        Long productId
) {
    public OptionCreateCommand(String name, Integer quantity) {
        this(name, quantity, null);
    }

    public Option toOption() {
        return new Option(name, quantity);
    }
}
