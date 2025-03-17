class ApplicationNotFoundException(BaseException):
    def __init__(self, message: str, code: str):
        super().__init__(message)
        self.code = code