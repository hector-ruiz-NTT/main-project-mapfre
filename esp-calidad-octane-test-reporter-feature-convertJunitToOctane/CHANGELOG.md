# Changelog

All notable changes to this project will be documented in this file.

## [1.0.0] - 2024-01-XX

### Added
- Initial release of ALM Octane Test Reporter GitHub Action
- Support for multiple test result formats (JUnit XML, TestNG XML, Jest JSON, Mocha JSON)
- OAuth2 authentication with ALM Octane
- Automatic CI server and pipeline registration
- Batch upload of test results for optimal performance
- Comprehensive input validation and error handling
- Detailed logging and debugging capabilities
- SSL certificate verification with skip option
- Configurable timeouts and retry mechanisms
- Support for custom pipeline and job names
- Output of key metrics (tests sent, passed, failed)
- Complete TypeScript implementation with type safety

### Security
- Secure handling of OAuth2 credentials
- Input sanitization to prevent injection attacks
- Optional SSL certificate verification
- Sensitive data masking in logs
