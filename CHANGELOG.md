# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [26.1.2.0]

### Added
### Fixed

- Ars Magica Legacy's `Affinity` no longer recurses forever in `toString`. The record prints its `Holder<Affinity>` opposites, and `Holder.Reference.toString` prints the affinity back, so any log line or exception message naming an affinity killed the server with a `StackOverflowError`.

### Removed
### Changed
