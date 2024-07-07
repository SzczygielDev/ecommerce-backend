package pl.szczygieldev.shared.architecture

interface CommandHandler<T : Command, J> {
      fun execute(command: T) : J
}