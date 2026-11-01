# Simple Project Structure Guide

## `common/`

Contains `ResourceNames`, `ResourceInventory`, and `LifeSupportSystem`. There are no enum files. Shared categories are represented as strings.

## `mission/`

Contains the common `Mission` class. Its mission type, state, resource names, and other labels are strings in this simple integrated version.

## `agents/`

Contains the abstract `Agent` parent, human crew classes, the abstract `Robot` parent, and robot subclasses. Agents answer capability questions through `canPerform` and `canHandle`.

## `facilities/`

Contains the base `Facility` and specialized facility classes. Facility types are strings passed to the base constructor.

## `emergency/`

Contains `Emergency`, which stores emergency type and state as strings.

## `colony/`

Contains `MarsColony`, which coordinates agents, facilities, resources, missions, environment, emergencies, and simulation time.

## `app/`

Contains `Main`, the testing class and application entry point.
